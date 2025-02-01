package spontaniius.ui.sign_up_confirm
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class SignUpConfirmViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _confirmState = MutableLiveData<Result<Boolean>>()
    val confirmState: LiveData<Result<Boolean>> = _confirmState

    fun confirmSignUp(username: String, confirmationCode: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.confirmSignUp(username, confirmationCode, password)
            _confirmState.value = result
        }
    }
}
