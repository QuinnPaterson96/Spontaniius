package spontaniius.data.repository

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.AutocompleteRequest
import spontaniius.data.remote.models.AutocompleteResponse
import spontaniius.data.remote.models.Circle
import spontaniius.data.remote.models.LatLngWrapper
import spontaniius.data.remote.models.LocationBias
import spontaniius.data.remote.models.PlaceDetailsResponse
import spontaniius.data.remote.models.PlaceSuggestion
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    fun fetchAutocomplete(query: String, location: LatLng?, apiKey: String): Flow<List<PlaceSuggestion>> = flow {
        if (query.length < 3) {
            emit(emptyList())
            return@flow
        }

        var locationFilter: LocationBias?

        if (location!=null){
            locationFilter = LocationBias(
                circle = Circle(
                    center = LatLngWrapper(location), // ✅ Accepts Google Maps LatLng
                    radius = 50000.0 // ✅ 50km radius
                )
            )
        }else{
            locationFilter=null
        }

        val request = AutocompleteRequest(input = query, locationBias = locationFilter, sessionToken = generateSessionToken())
        val result = remoteDataSource.getAutocompleteResults(request, apiKey = apiKey)

        Log.d("PlacesRepository", "Raw result: $result") // 🚀 Debug API result

        result.fold(
            onSuccess = {
                Log.d("PlacesRepository", "Success: ${it.suggestions}") // ✅ Log successful response
                emit(it.suggestions)
            },
            onFailure = {
                Log.e("PlacesRepository", "API call failed: ${it.message}") // ✅ Log error
            }
        )
    }.flowOn(Dispatchers.IO)

    suspend fun getPlaceDetails(placeId: String, apiKey: String): Result<PlaceDetailsResponse> {
        return remoteDataSource.getDetailsFromPlaceId(placeId, apiKey)
    }


    private fun generateSessionToken(): String {
        return UUID.randomUUID().toString()
    }
}
