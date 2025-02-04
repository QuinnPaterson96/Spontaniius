package spontaniius.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class PhoneLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginError = MutableLiveData<Throwable?>()
    val loginError: LiveData<Throwable?> = _loginError

    fun login(phoneNumber: String, password: String) {
        viewModelScope.launch {
            val result: Result<Boolean> = authRepository.signIn(phoneNumber, password)
            result.exceptionOrNull()?.let { error ->
                _loginError.postValue(error) // ✅ Emit only error
            }
        }
    }
}
