package spontaniius.ui.password_reset

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import spontaniius.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val resetPasswordResult: LiveData<Boolean> = authRepository.resetPasswordResult
    val confirmPasswordResult: LiveData<Boolean> = authRepository.confirmPasswordResult
    val resetError: LiveData<String> = authRepository.resetError

    fun resetPassword(phoneNumber: String) {
        authRepository.resetPassword(phoneNumber)
    }

    fun confirmResetPassword(username: String, newPassword: String, confirmationCode: String) {
        authRepository.confirmResetPassword(username, newPassword, confirmationCode)
    }
}
