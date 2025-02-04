package spontaniius.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // Expose LiveData from repository
    val userAttributes: LiveData<JSONObject?> = userRepository.userDetails

    // Refresh user data manually if needed
    fun refreshUserAttributes() {
        userRepository.fetchUserAttributes()
    }
}
