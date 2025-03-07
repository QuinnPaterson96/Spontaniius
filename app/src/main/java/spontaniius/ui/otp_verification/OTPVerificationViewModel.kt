package spontaniius.ui.otp_verification

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class OTPVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _verificationStatus = MutableLiveData<Result<String>>()
    val verificationStatus: LiveData<Result<String>> = _verificationStatus

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Verifies OTP and signs in the user.
     */
    fun verifyOtp(verificationId: String, otpCode: String, activity: Activity) {
        viewModelScope.launch {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            authRepository.verifyOtp(verificationId, otpCode, activity).onSuccess { userResponse ->
                _verificationStatus.postValue(Result.success(userResponse))
            }.onFailure { e ->
                _error.postValue(e.localizedMessage)
            }
        }
    }

    /**
     * Resends OTP.
     */
    fun resendOtp(phoneNumber: String, activity: Activity) {
        authRepository.sendOtp(phoneNumber, activity) { result ->
            result.onSuccess { newVerificationId ->
                _verificationStatus.postValue(Result.success(newVerificationId))
            }.onFailure { e ->
                _error.postValue(e.localizedMessage)
            }
        }
    }
}
