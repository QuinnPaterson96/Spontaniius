package spontaniius.data.repository

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.spontaniius.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val remoteDataSource: RemoteDataSource
) {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    /**
     * ✅ **Check if user is signed in**
     */
    fun isUserSignedIn(): String? = firebaseAuth.currentUser?.uid

    /**
     * ✅ **Get Google Sign-In Intent**
     */

    /**
     * ✅ **Handle Google Sign-In & return `externalId`**
     */
    suspend fun handleGoogleSignInResult(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val firebaseUid = signInWithFirebase(credential)
            firebaseUid?.let { Result.success(it) } ?: Result.failure(Exception("Firebase sign-in failed"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google Sign-In failed", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithFirebase(credential: com.google.firebase.auth.AuthCredential): String? {
        return try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user?.uid // Returns Firebase UID
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Firebase authentication failed", e)
            null
        }
    }

    /**
     * ✅ **Handle Firebase sign-in & return `externalId`**
     */
    private suspend fun signInWithFirebase(activity: Activity, credential: AuthCredential): String? {
        return suspendCancellableCoroutine { continuation ->
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        continuation.resume(firebaseAuth.currentUser?.uid)
                    } else {
                        continuation.resume(null)
                    }
                }
        }
    }

    /**
     * ✅ **Send OTP for phone verification**
     */
    fun sendOtp(phoneNumber: String, activity: Activity, callback: (Result<String>) -> Unit) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // ✅ Do NOT sign in here. Let the user enter OTP manually.
            }

            override fun onVerificationFailed(e: FirebaseException) {
                callback(Result.failure(e))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                forceResendingToken = token
                callback(Result.success(verificationId)) // ✅ Pass verificationId
            }
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        forceResendingToken?.let { optionsBuilder.setForceResendingToken(it) }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }


    /**
     * ✅ **Verify OTP & return `externalId`**
     */
    suspend fun verifyOtp(verificationId: String, otpCode: String, activity: Activity): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val firebaseUid = firebaseAuth.currentUser?.uid
                        firebaseUid?.let { continuation.resume(Result.success(it)) }
                            ?: continuation.resume(Result.failure(Exception("Firebase UID is null")))
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Unknown error"
                        continuation.resume(Result.failure(Exception(errorMessage)))
                    }
                }
        }
    }


    /**
     * ✅ **Log out user**
     */
    fun signOut() {
        firebaseAuth.signOut()
    }
}
