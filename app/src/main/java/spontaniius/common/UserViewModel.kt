package spontaniius.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import spontaniius.data.remote.models.UserResponse
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Expose LiveData from repository
    val userAttributes: LiveData<UserResponse?> = userRepository.userDetails

    // Refresh user data manually if needed
    fun refreshUserAttributes() {
        viewModelScope.launch {
            userRepository.fetchUserDetails()
        }
    }
}
