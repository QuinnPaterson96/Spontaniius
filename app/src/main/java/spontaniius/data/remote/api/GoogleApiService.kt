package spontaniius.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import spontaniius.data.remote.models.GoogleMapsResponse

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

}
