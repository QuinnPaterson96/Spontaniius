package spontaniius.ui.phone_login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import spontaniius.data.repository.AuthRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PhoneLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _verificationId = MutableLiveData<String?>()
    val verificationId: LiveData<String?> = _verificationId

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    /**
     * Sends OTP to the provided phone number.
     */
    fun sendOtp(phoneNumber: String, activity: Activity, callback: (Result<String>) -> Unit) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("PhoneLoginViewModel", "Auto Verification Completed")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("PhoneLoginViewModel", "OTP Verification Failed: ${e.localizedMessage}")
                _error.postValue(e.localizedMessage)
                callback(Result.failure(e))
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("PhoneLoginViewModel", "OTP Sent: $verificationId")
                _verificationId.postValue(verificationId)
                forceResendingToken = token
                callback(Result.success(verificationId))
            }
        }

        val optionsBuilder = PhoneAuthOptions.newBuilder(authRepository.firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)

        forceResendingToken?.let { optionsBuilder.setForceResendingToken(it) }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
}
