package spontaniius.ui.create_event

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.remote.models.EventResponse
import spontaniius.data.repository.EventRepository
import spontaniius.data.repository.LocationRepository
import spontaniius.domain.models.Event
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val locationRepository: LocationRepository,

    ) : ViewModel() {

    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> = _location

    private val _locationPermissionNeeded = MutableLiveData<Boolean>()
    val locationPermissionNeeded = _locationPermissionNeeded

    private val _eventCreated: MutableLiveData<Event?> = MutableLiveData<Event?>()
    val eventCreated: LiveData<Event?> = _eventCreated

    private val _address = MutableLiveData<String?>()
    val address: LiveData<String?> = _address

    /**
     * Calls the repository to create an event.
     */
    fun createEvent(
        title: String,
        description: String,
        gender: String,
        icon: String,
        event_starts: String,  // ISO 8601 format: YYYY-MM-DDTHH:mm:ssZ
        event_ends: String,
        latLng: LatLng,
        max_radius: Int,
    ) {

        viewModelScope.launch {
            val result: Result<EventResponse> = eventRepository.createEvent(
                title = title,
                description = description,
                gender = gender,
                icon = icon,
                event_starts = event_starts,
                event_ends = event_ends,
                latLng = latLng,
                max_radius = max_radius
            )
            result.onSuccess { value: EventResponse ->
                _eventCreated.postValue(value.toDomain())
            }
            result.onFailure { error ->
                Log.e("Create Event",error.toString())
                _eventCreated.postValue(null)
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

    fun getAddressFromLocation(latLng: LatLng, apiKey: String) {
        viewModelScope.launch {
            val result = locationRepository.getAddressFromCoordinates(latLng, apiKey)
            result.onSuccess { googleLocationResponse ->
                if (googleLocationResponse.results != null) {
                    _address.postValue(googleLocationResponse.results[0].formatted_address)
                } else {
                    _address.postValue(null)
                }
            }
            result.onFailure {
                _address.postValue(null)
            }
        }
    }
}
