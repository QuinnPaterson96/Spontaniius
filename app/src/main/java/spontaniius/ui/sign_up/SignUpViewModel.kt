package spontaniius.ui.sign_up


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.AuthRepository
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signUpState = MutableLiveData<Result<String>>()
    val signUpState: LiveData<Result<String>> = _signUpState

    fun signUp(name: String, gender: String, phone: String, password: String) {
        val randomSuffix = Random().nextInt(100000).toString()
        val username = name.replace(" ", "") + randomSuffix

        viewModelScope.launch {
            val result = authRepository.signUp(username, password, name, gender, phone)
            _signUpState.value = result
        }
    }
}
