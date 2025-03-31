package spontaniius.data.remote.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import spontaniius.data.remote.models.AutocompleteRequest
import spontaniius.data.remote.models.AutocompleteResponse
import spontaniius.data.remote.models.GeocodingResponse
import spontaniius.data.remote.models.GoogleMapsResponse
import spontaniius.data.remote.models.PlaceDetailsResponse

interface GoogleApiService {

    @GET("maps/api/geocode/json")
    suspend fun getLocationFromAddress(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): Response<GoogleMapsResponse>

    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String
    ): Response<GoogleMapsResponse>

    @GET("maps/api/geocode/json")
    suspend fun getAddressFromCoordinates(
        @Query("latlng") latlng: String, // Format: "latitude,longitude"
        @Query("key") apiKey: String
    ): Response<GeocodingResponse>


}
