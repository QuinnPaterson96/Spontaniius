package spontaniius.common


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import spontaniius.data.remote.models.PlaceDetailsResponse
import spontaniius.data.remote.models.PlaceSuggestion
import spontaniius.data.repository.PlacesRepository
import javax.inject.Inject


@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel(){

    private val _places = MutableStateFlow<List<PlaceSuggestion>>(emptyList())
    val places: StateFlow<List<PlaceSuggestion>> = _places

    private val _placeDetails = MutableLiveData<PlaceDetailsResponse?>()
    val placeDetails: LiveData<PlaceDetailsResponse?> = _placeDetails




    fun searchPlaces(query: String, location: LatLng?, apiKey: String) {
        viewModelScope.launch {
            placesRepository.fetchAutocomplete(query = query, location = location, apiKey = apiKey).collectLatest { suggestions ->
                _places.value = suggestions
            }
        }
    }

    fun getPlaceDetails(placeId: String, apiKey: String){
        viewModelScope.launch {
            val result = placesRepository.getPlaceDetails(placeId, apiKey)

            result.onSuccess { placeDetails ->
                _placeDetails.postValue(placeDetails)
                Log.d("PlacesViewModel", "Fetched place details: $placeDetails")
            }.onFailure { error ->
                // ❌ Handle error
                _placeDetails.postValue(null)
                Log.e("PlacesViewModel", "Error fetching place details: ${error.message}")
            }
        }
    }
}
