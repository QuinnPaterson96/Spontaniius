package spontaniius.common

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import spontaniius.data.remote.models.UserResponse
import spontaniius.data.repository.UserRepository
import spontaniius.domain.models.User
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Expose LiveData from repository
    val _userAttributes: MutableLiveData<User?> = MutableLiveData<User?>()
    val userAttributes: LiveData<User?> = _userAttributes


    fun refreshUserAttributes(externalId: String) {
        viewModelScope.launch {
            val result = userRepository.fetchUserDetails(externalId = externalId)
            result.onSuccess { userResponse ->
                _userAttributes.postValue(userResponse.toDomainModel()) // ✅ User exists
            }
            result.onFailure { error ->
                if (error is HttpException && error.code() == 404) {
                    _userAttributes.postValue(null) // ✅ User does not exist
                } else {
                    Log.e("CheckNewUser", "Error fetching user details", error)
                }
            }
        }
    }
}
