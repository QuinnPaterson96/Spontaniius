package spontaniius.ui.user_menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.local.dao.UserDao
import spontaniius.data.remote.models.UserResponse
import spontaniius.data.repository.AuthRepository
import spontaniius.data.repository.UserRepository
import spontaniius.domain.models.User
import javax.inject.Inject

@HiltViewModel
class UserOptionsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val userDao: UserDao
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userUpdated = MutableLiveData<Boolean>()
    val userUpdated: LiveData<Boolean> = _userUpdated

    private val _accountDeleted = MutableLiveData<Boolean>()
    val accountDeleted: LiveData<Boolean> = _accountDeleted

    /**
     * Load user details from backend
     */
    fun loadUser() {
        _isLoading.value = true
        viewModelScope.launch {
            try{
            _user.postValue(userDao.getUser()?.toDomainModel())
            }finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update user details and send to backend
     */
    fun updateUser(name: String, phone: String, gender: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.updateUser(name, phone, gender)
            result.onSuccess {
                _userUpdated.postValue(true)
            }
            _isLoading.value = false

        }
    }

    fun deleteUser() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = userDao.getUser()
                if (user != null) {
                    val result = userRepository.deleteUser()
                    result.onSuccess {
                        authRepository.signOut()
                        _accountDeleted.postValue(true)
                    }
                    result.onFailure { error ->
                        Log.e("User Options", error.toString())
                    }
                }
            }finally {
                _isLoading.value = false
            }
        }
    }
}
