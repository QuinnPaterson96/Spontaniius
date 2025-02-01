package spontaniius.data.repository

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.spontaniius.BuildConfig
import com.spontaniius.R
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@ActivityScoped
class AuthRepository @Inject constructor(private val activity: Activity) {

    private val _authResult = MutableLiveData<Boolean>()
    val authResult: LiveData<Boolean> = _authResult

    private val _resetPasswordResult = MutableLiveData<Boolean>()
    val resetPasswordResult: LiveData<Boolean> = _resetPasswordResult

    private val _resetError = MutableLiveData<String>()
    val resetError: LiveData<String> = _resetError

    private val _confirmPasswordResult = MutableLiveData<Boolean>()
    val confirmPasswordResult: LiveData<Boolean> = _confirmPasswordResult

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(activity, gso)
    }

    fun getGoogleSignInIntent(): Intent = googleSignInClient.signInIntent

    fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                authenticateWithAWS(idToken)
            } else {
                _authResult.postValue(false)
            }
        } catch (e: ApiException) {
            Log.e("AuthRepository", "Google sign-in failed", e)
            _authResult.postValue(false)
        }
    }

    private fun authenticateWithAWS(idToken: String) {
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), activity,
            { result ->
                Log.i("AuthRepository", "Sign in successful: $result")
                _authResult.postValue(true)
            },
            { error ->
                Log.e("AuthRepository", "Sign in failed", error)
                _authResult.postValue(false)
            }
        )
    }

    fun resetPassword(phoneNumber: String) {
        Amplify.Auth.resetPassword(
            phoneNumber,
            { result: AuthResetPasswordResult ->
                Log.i("AuthRepository", "Reset Password Result: $result")
                _resetPasswordResult.postValue(true)
            },
            { error: AuthException ->
                Log.e("AuthRepository", error.toString())
                _resetError.postValue("Error: ${error.message}")
            }
        )
    }

    fun confirmResetPassword(username: String, newPassword: String, confirmationCode: String) {
        Amplify.Auth.confirmResetPassword(
            username,
            newPassword,
            confirmationCode,
            {
                Log.i("AuthRepository", "Password Reset Confirmed")
                _confirmPasswordResult.postValue(true)
            },
            { error ->
                Log.e("AuthRepository", error.toString())
                _resetError.postValue("Error: ${error.message}")
            }
        )
    }

    suspend fun signIn(phoneNumber: String, password: String): Result<Boolean> {
        return suspendCoroutine { continuation ->
            Amplify.Auth.signIn(
                phoneNumber,
                password,
                {
                    Log.i("AuthRepository", "Sign in successful")
                    continuation.resume(Result.success(true)) // ✅ Return Boolean
                },
                { error ->
                    Log.e("AuthRepository", "Sign in failed", error)
                    continuation.resume(Result.failure(error))
                }
            )
        }
    }

    suspend fun signUp(
        username: String,
        password: String,
        name: String,
        gender: String,
        phoneNumber: String
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val attributeList = listOf(
                    AuthUserAttribute(AuthUserAttributeKey.name(), name),
                    AuthUserAttribute(AuthUserAttributeKey.gender(), gender),
                    AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), phoneNumber)
                )

                val options = AuthSignUpOptions.builder().userAttributes(attributeList).build()

                Amplify.Auth.signUp(username, password, options,
                    { result ->
                        val userId = result.userId ?: "" // Ensure non-nullability
                        Log.i("AuthRepository", "Sign up successful: $userId")
                        continuation.resume(Result.success(userId))
                    },
                    { error ->
                        Log.e("AuthRepository", "Sign up failed", error)
                        continuation.resume(Result.failure(error)) // Resume coroutine on failure
                    }
                )
            }
        }
    }


    suspend fun confirmSignUp(username: String, confirmationCode: String, password: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val confirmResult: Result<Boolean> = suspendCoroutine { continuation ->
                    Amplify.Auth.confirmSignUp(username, confirmationCode,
                        { result ->
                            if (result.isSignUpComplete) {
                                continuation.resume(Result.success(true))
                            } else {
                                continuation.resume(Result.failure(Exception("Sign-up confirmation not complete")))
                            }
                        },
                        { error ->
                            continuation.resume(Result.failure(error))
                        }
                    )
                }

                // If sign-up confirmation is successful, attempt to sign in
                if (confirmResult.isSuccess) {
                    val signInResult = signIn(username, password)
                    if (signInResult.isSuccess) {
                        Log.i("AuthRepository", "User confirmed and signed in successfully")
                        Result.success(true)
                    } else {
                        Result.failure(Exception("Sign-in after confirmation failed"))
                    }
                } else {
                    confirmResult
                }
            } catch (error: Exception) {
                Log.e("AuthRepository", "Sign-up confirmation failed", error)
                Result.failure(error)
            }
        }
    }



}

