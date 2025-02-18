package spontaniius.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.spontaniius.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CreateUserRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private  val remoteDataSource: RemoteDataSource
) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isUserSignedIn = MutableLiveData<Boolean>()
    val isUserSignedIn: LiveData<Boolean> = _isUserSignedIn

    private val _phoneVerificationId = MutableLiveData<String?>()
    val phoneVerificationId: LiveData<String?> = _phoneVerificationId

    private val _verificationError = MutableLiveData<String?>()
    val verificationError: LiveData<String?> = _verificationError

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID) // Ensure this matches Firebase
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null


    /**
     * Check if the user is already signed in.
     */
    fun checkAuthState() {
        _isUserSignedIn.postValue(firebaseAuth.currentUser != null)
    }

    /**
     * Get the Google Sign-In Intent.
     */
    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    /**
     * Handle Google Sign-In and authenticate with Firebase.
     */
    suspend fun handleGoogleSignInResult(data: Intent?, activity: Activity): Result<Unit> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            val userName = account?.displayName
            val externalId = account?.id

            if (idToken != null && userName != null && externalId != null) {
                val credential = GoogleAuthProvider.getCredential(idToken, null)

                val authResult = signInWithFirebase(activity, credential)

                return if (authResult) {
                    createUserInBackend(name = userName, authProvider = "Google", externalId = externalId)
                } else {
                    Result.failure(Exception("Firebase sign-in failed"))
                }
            } else {
                Result.failure(Exception("Google Sign-In data missing"))
            }
        } catch (e: ApiException) {
            Log.e("AuthRepository", "Google Sign-In failed", e)
            Result.failure(e)
        }
    }

    private suspend fun signInWithFirebase(activity: Activity, credential: com.google.firebase.auth.AuthCredential): Boolean {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    continuation.resume(task.isSuccessful)
                }
        }
    }

    /**
     * Send OTP for phone number verification.
     */
    fun sendOtp(phoneNumber: String, activity: Activity) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            Log.i("AuthRepository", "Phone Sign-In successful")
                            _isUserSignedIn.postValue(true)
                        } else {
                            Log.e("AuthRepository", "Phone Sign-In failed", task.exception)
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("AuthRepository", "OTP Verification failed", e)
                _verificationError.postValue(e.localizedMessage)
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.i("AuthRepository", "OTP Sent to: $phoneNumber")
                _phoneVerificationId.postValue(verificationId)
                forceResendingToken = token // ✅ Save token for resending
            }
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        forceResendingToken?.let {
            optionsBuilder.setForceResendingToken(it) // ✅ Use saved token if resending
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    /**
     * Verify OTP and sign in the user.
     */
    fun verifyOtp(verificationId: String, otpCode: String, activity: Activity) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.i("AuthRepository", "Phone Sign-In successful")
                    _isUserSignedIn.postValue(true)
                } else {
                    val errorMessage = task.exception?.localizedMessage ?: "Unknown error"
                    Log.e("AuthRepository", "Phone Sign-In failed: $errorMessage")
                    _isUserSignedIn.postValue(false)
                    _verificationError.postValue(errorMessage) // ✅ Post error for UI display
                }
            }
    }


    /**
     * Log out the user.
     */
    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        _isUserSignedIn.postValue(false)
        _phoneVerificationId.postValue(null) // ✅ Reset phone verification ID
        _verificationError.postValue(null) // ✅ Reset verification error
        Log.i("AuthRepository", "User signed out")
    }


    private suspend fun createUserInBackend(name: String, externalId: String, authProvider: String) : Result<Unit>{

            val request = CreateUserRequest(
                name = name,
                phone = "",  // Placeholder (Google Sign-In doesn't provide phone)
                gender = null,
                card_id = null,
                external_id = externalId,
                auth_provider = authProvider
            )

            return remoteDataSource.createUser(request)

    }
}
