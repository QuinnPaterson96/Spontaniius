package spontaniius.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import spontaniius.data.remote.models.AutocompleteRequest
import spontaniius.data.remote.models.AutocompleteResponse
import spontaniius.data.remote.models.GeocodingResponse
import spontaniius.data.remote.models.GoogleMapsResponse
import spontaniius.data.remote.models.PlaceDetailsResponse


interface PlacesApiService {
    @Headers(
        "Content-Type: application/json",
        "X-Goog-FieldMask: suggestions.placePrediction.placeId,suggestions.placePrediction.text.text"
    )
    @POST
    suspend fun getAutocompleteResults(
        @Url fullUrl: String, // ✅ Pass the full URL dynamically, // ✅ Encode colon
        @Body request: AutocompleteRequest,
        @retrofit2.http.Header("X-Goog-Api-Key") apiKey: String
    ): Response<AutocompleteResponse>

    @GET("places/{placeId}")
    suspend fun getPlaceDetails(
        @Path("placeId") placeId: String,
        @Header("X-Goog-Api-Key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String = "location,formattedAddress"
    ): Response<PlaceDetailsResponse>
}