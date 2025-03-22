package spontaniius.ui.find_event

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.EventRepository
import spontaniius.data.repository.LocationRepository
import spontaniius.domain.models.Event
import javax.inject.Inject

@HiltViewModel
class FindEventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val locationRepository: LocationRepository,
) : ViewModel(){

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    private val _eventError = MutableLiveData<Boolean>()
    val eventError: LiveData<Boolean> get() = _eventError

    private val _currentLocation = MutableLiveData<LatLng?>()
    val currentLocation: LiveData<LatLng?> get() = _currentLocation

    private val _locationPermissionNeeded = MutableLiveData<Boolean>()
    val locationPermissionNeeded = _locationPermissionNeeded

    fun getCurrentLocation() {
        viewModelScope.launch {
            val location = locationRepository.fetchUserLocation(
                onSuccess = {
                    _currentLocation.postValue(LatLng(it.latitude, it.longitude))
                },

                onFailure = {
                    locationPermissionNeeded.postValue(true)
                })
        }
    }

    fun fetchEvents(lat: Double, lng: Double, gender: String?) {
        viewModelScope.launch {
            val result = eventRepository.getNearbyEvents(lat, lng, gender)
            result.onSuccess { eventList ->
                _events.postValue(eventList)
            }
            result.onFailure { exception ->
                Log.e("Find Event Fragment", exception.toString())
                _eventError.postValue(true)
            }
        }
    }

    fun permissionGranted() {
        _locationPermissionNeeded.postValue(false) // Reset permission state after granted
    }

    fun getLocationFromAddress(address: String, apiKey: String) {
        viewModelScope.launch {
            val result = locationRepository.getLocationFromAddress(address, apiKey)
            result.onSuccess { latLng ->
                _currentLocation.postValue(latLng)
            }
            result.onFailure {
                //#TODO
            }
        }
    }
}
