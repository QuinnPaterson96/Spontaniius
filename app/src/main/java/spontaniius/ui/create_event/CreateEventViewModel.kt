package spontaniius.ui.create_event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.remote.models.CreateEventRequest
import spontaniius.data.remote.models.EventResponse
import spontaniius.data.repository.EventRepository
import spontaniius.data.repository.LocationRepository
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val locationRepository: LocationRepository

) : ViewModel() {
    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> = _location

    private val _locationPermissionNeeded = MutableLiveData<Boolean>()
    val locationPermissionNeeded = _locationPermissionNeeded

    private val _eventCreated = MutableLiveData<EventResponse>()
    val eventCreatedStatus = _eventCreated

    /**
     * Calls the repository to create an event.
     */
    fun createEvent(request: CreateEventRequest) {
        viewModelScope.launch {
            val result: Result<EventResponse> = eventRepository.createEvent(request)
            result.onSuccess { value: EventResponse ->
                eventCreatedStatus.postValue(value)
            }
            result.onFailure {
                // TODO: Error Handling
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            locationRepository.fetchUserLocation(
                onSuccess = { latLng -> _location.postValue(latLng) },
                onFailure = { locationPermissionNeeded.postValue(true) }
            )
        }
    }

    fun permissionGranted() {
        _locationPermissionNeeded.postValue(false) // Reset permission state after granted
    }

    fun getLocationFromAddress(address: String, apiKey: String) {
        viewModelScope.launch {
            val result = locationRepository.getLocationFromAddress(address, apiKey)
            result.onSuccess { latLng ->
                _location.postValue(latLng)
            }
            result.onFailure {
                //#TODO
            }
        }
    }

}
