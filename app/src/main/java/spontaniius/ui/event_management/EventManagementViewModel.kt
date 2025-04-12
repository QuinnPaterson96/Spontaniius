package spontaniius.ui.event_management

import android.util.Log
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
import kotlin.math.log

@HiltViewModel
class EventManagementViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private val _userDetails: MutableLiveData<User?> = MutableLiveData<User?>()
    val userDetails: LiveData<User?> = _userDetails

    private val _event_ended = MutableLiveData<Boolean>()
    val eventEnded = _event_ended

    private val _eventDetails = MutableLiveData<Event?>()
    val eventDetails: LiveData<Event?> get() = _eventDetails


    fun endEvent(eventId: Int) {
        viewModelScope.launch {
            val result = eventRepository.endEvent(eventId)
            result.onSuccess {
                _event_ended.postValue(true)
            }
            result.onFailure {
                _event_ended.postValue(true)
                Log.e("Event Management", "Issue ending the event")
            }
        }
    }

    fun add15Mins(eventId: Int, currentEndTime: String) {
        viewModelScope.launch {
            val result = eventRepository.extendEvent15Mins(eventId, currentEndTime)
            result.onSuccess { event ->
                _eventDetails.postValue(event.toDomain())
            }
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
            result.onFailure { error ->
                Log.e("Manage Event Fragment", error.toString())
                _eventDetails.postValue(null) } // Handle error
        }
    }

    fun getGoogleMapsUrl(lat: Double, lng: Double): String? {
        return eventRepository.getGoogleMapsUrl(lat = lat, lng = lng)
    }
}
