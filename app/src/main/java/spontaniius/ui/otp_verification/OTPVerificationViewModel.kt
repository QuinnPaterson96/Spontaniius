package spontaniius.ui.otp_verification

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import spontaniius.data.repository.AuthRepository
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class OTPVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _newUser = MutableLiveData<Boolean>()
    val newUser = _newUser

    private val _firebaseId = MutableLiveData<String>()
    val firebaseId: LiveData<String> = _firebaseId

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Verifies OTP and signs in the user.
     */
    fun verifyOtp(verificationId: String, otpCode: String, activity: Activity) {
        viewModelScope.launch {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            authRepository.verifyOtp(verificationId, otpCode, activity).onSuccess { externalId ->
                _firebaseId.postValue(externalId)

            }.onFailure { e ->
                _error.postValue(e.localizedMessage)
            }
        }
    }

    fun checkNewUser(externalId: String) {
        viewModelScope.launch {
            val result = userRepository.fetchUserDetails(externalId = externalId)
            result.onSuccess {
                _newUser.postValue(false) // ✅ User exists
            }
            result.onFailure { error ->
                if (error is HttpException && error.code() == 404) {
                    _newUser.postValue(true) // ✅ User does not exist
                } else {
                    Log.e("CheckNewUser", "Error fetching user details", error)
                }
            }
        }
    }


    /**
     * Resends OTP.
     */
    fun resendOtp(phoneNumber: String, activity: Activity) {
        authRepository.sendOtp(phoneNumber, activity) { result ->
            result.onSuccess { newVerificationId ->
                _firebaseId.postValue(newVerificationId)
            }.onFailure { e ->
                _error.postValue(e.localizedMessage)
            }
        }
    }
}
