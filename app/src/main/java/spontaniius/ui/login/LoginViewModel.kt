package spontaniius.ui.login

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import spontaniius.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository) : ViewModel() {

    private val _newUser = MutableLiveData<Boolean>()
    val newUser = _newUser

    private val _firebaseId = MutableLiveData<String>()
    val firebaseId: LiveData<String> = _firebaseId

    fun handleGoogleSignInResult(idToken: String){
        viewModelScope.launch {
            val firebase_result = authRepository.handleGoogleSignInResult(idToken)
            firebase_result.onSuccess { firebaseId ->
                checkNewUser(firebaseId)
                _firebaseId.postValue(firebaseId)
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

}