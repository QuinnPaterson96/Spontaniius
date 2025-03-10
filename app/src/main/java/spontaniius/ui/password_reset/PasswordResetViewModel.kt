package spontaniius.ui.password_reset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import spontaniius.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val _resetError: MutableLiveData<String> = MutableLiveData<String>()
    val _resetPasswordResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val _confirmPasswordResult: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val resetPasswordResult: LiveData<Boolean> = _resetPasswordResult
    val confirmPasswordResult: LiveData<Boolean> = _confirmPasswordResult
    val resetError: LiveData<String> = _resetError

    fun resetPassword(phoneNumber: String) {

    }

    fun confirmResetPassword(username: String, newPassword: String, confirmationCode: String) {
    }
}
