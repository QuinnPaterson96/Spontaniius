package spontaniius.ui.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.remote.models.CreateUserRequest
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _signUpResult = MutableLiveData<Result<Unit>>()
    val signUpResult: LiveData<Result<Unit>> = _signUpResult

    fun registerUser(externalId: String, authProvider: String, name: String, gender: String) {
        viewModelScope.launch {
            val request = CreateUserRequest(
                name = name,
                phone = "",  // Firebase does not provide phone number for all auth methods
                gender = gender,
                external_id = externalId,
                auth_provider = authProvider
            )

            val result = userRepository.createUser(request)
            _signUpResult.postValue(result.map { Unit }) // ✅ Convert result to `Unit`
        }
    }
}
