package spontaniius.ui.event_management

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.remote.models.EventResponse
import spontaniius.data.remote.models.UserResponse
import spontaniius.data.repository.EventRepository
import spontaniius.data.repository.UserRepository
import spontaniius.domain.models.Event
import spontaniius.domain.models.User
import javax.inject.Inject

@HiltViewModel
class EventManagementViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private val _userDetails: MutableLiveData<User?> = MutableLiveData<User?>()
    val userDetails: LiveData<User?> = _userDetails

    private val _event_joined = MutableLiveData<Boolean>()
    val eventJoined = _event_joined

    private val _eventDetails = MutableLiveData<Event?>()
    val eventDetails: LiveData<Event?> get() = _eventDetails

    fun joinEvent(eventId: Int){

    }

    fun endEvent(eventId: Int, onSuccess: (EventResponse) -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            val result = eventRepository.endEvent(eventId)
            result.onSuccess {  }
            result.onFailure {  }
        }
    }

    fun add15Mins(eventId: Int, currentEndTime: String) {
        viewModelScope.launch {
            val result = eventRepository.extendEvent15Mins(eventId, currentEndTime)
            result.onSuccess { }
            result.onFailure { }
        }
    }

    fun getUserDetails() {
        viewModelScope.launch {
            _userDetails.postValue(userRepository.getUserFromLocal())
        }
    }

    /**
     * Fetch event details and update LiveData.
     */
    fun loadEventDetails(eventId: Int) {
        viewModelScope.launch {
            val result: Result<Event> = eventRepository.fetchEventDetails(eventId)
            result.onSuccess { _eventDetails.postValue(it) }
            result.onFailure { _eventDetails.postValue(null) } // Handle error
        }
    }

    fun getGoogleMapsUrl(streetAddress: String?): String? {
        return eventRepository.getGoogleMapsUrl(streetAddress)
    }
}
