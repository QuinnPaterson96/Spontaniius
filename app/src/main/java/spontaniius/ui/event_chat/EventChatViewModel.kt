package spontaniius.ui.event_chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.UserRepository
import spontaniius.domain.models.User
import javax.inject.Inject

@HiltViewModel
class EventChatViewModel @Inject constructor(
    val userRepository: UserRepository
) : ViewModel() {
    private val _userDetails: MutableLiveData<User?> = MutableLiveData<User?>()
    val userDetails: LiveData<User?> = _userDetails

    fun getUserDetails() {
        viewModelScope.launch {
            _userDetails.postValue(userRepository.getUserFromLocal())
        }
    }
}
