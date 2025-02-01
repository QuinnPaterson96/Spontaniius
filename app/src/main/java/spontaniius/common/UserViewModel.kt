package spontaniius.common  // Ensure this matches your folder structure

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userAttributes = MutableLiveData<JSONObject?>()
    val userAttributes: LiveData<JSONObject?> get() = _userAttributes

    fun fetchUserAttributes() {
        viewModelScope.launch {
            val attributes = userRepository.getCurrentUserAttributes()

            if (attributes == null) {
                Log.w("UserViewModel", "User is not authenticated, cannot fetch attributes")
            }

            _userAttributes.postValue(attributes) // Can be null
        }
    }
}
