package spontaniius.data.remote

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import spontaniius.data.remote.api.ApiService
import spontaniius.data.remote.api.GoogleApiService
import spontaniius.data.remote.api.PlacesApiService
import spontaniius.data.remote.models.*
import spontaniius.domain.models.Event
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val googleApiService: GoogleApiService,  // Google Maps API
    private val placesApiService: PlacesApiService
) {

    /** EVENTS **/
    suspend fun createEvent(request: CreateEventRequest): Result<EventResponse> = safeApiCall {
        apiService.createEvent(request)
    }

    suspend fun joinEvent(request: JoinEventRequest): Result<EventResponse> = safeApiCall {
        apiService.joinEvent(request)
    }

    suspend fun getNearbyEvents(request: FindEventRequest): Result<List<EventResponse>> {
        return try {
            val response = apiService.getNearbyEvents(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch events: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getEventById(eventId: Int): Result<EventResponse> = safeApiCall {
        apiService.getEventById(eventId)
    }

    suspend fun extendEvent(eventId: Int, request: ExtendEventRequest): Result<EventResponse> {
        return safeApiCall { apiService.extendEvent(eventId, request) }
    }

    suspend fun endEvent(eventId: Int): Result<EventResponse> {
        return safeApiCall { apiService.endEvent(eventId) }
    }

    /**
     * Update an event's card IDs.
     */
    suspend fun updateEventCards(eventId: Int, request: UpdateEventCardsRequest): Result<EventResponse> {
        return safeApiCall { apiService.updateEventCards(eventId, request) }
    }

    /** PROMOTIONS **/
    suspend fun createPromotion(request: PromotionCreateRequest): Result<Map<String, Any>> = safeApiCall {
        apiService.createPromotion(request)
    }

    suspend fun getAllPromotions(): Result<List<Map<String, Any>>> = safeApiCall {
        apiService.getAllPromotions()
    }

    suspend fun getPromotionById(promotionId: Int): Result<Map<String, Any>> = safeApiCall {
        apiService.getPromotionById(promotionId)
    }


    /** CARDS **/
    suspend fun createCard(request: CardCreateRequest): Result<CardResponse> = safeApiCall {
        apiService.createCard(request)
    }

    suspend fun getCardById(cardId: Int): Result<Map<String, Any>> = safeApiCall {
        apiService.getCardById(cardId)
    }


    /** CARD COLLECTIONS **/
    suspend fun createOrUpdateCardCollection(collectionRequest: CardCollectionRequest): Result<CardCollectionResponse> = safeApiCall {
        apiService.createOrUpdateCardCollection(collectionRequest)
    }

    suspend fun getUserCardCollection(userId: String): Result<List<Any>> = safeApiCall {
        apiService.getUserCardCollections(userId)
    }

    suspend fun getCardDetails(cardIds: List<Int>): Result<List<CardResponse>> = safeApiCall {
        apiService.getCardDetails(GetCardsRequest(cardIds))
    }

    suspend fun getUserCardCollections(userId: String): Result<List<CardCollectionResponse>> = safeApiCall {
        apiService.getUserCardCollections(userId)
    }

    /** REPORTS **/
    suspend fun reportUser(reportRequest: ReportRequest): Result<ReportResponse> = safeApiCall {
        apiService.reportUser(reportRequest)
    }


    /** USERS **/
    suspend fun checkUserExists(externalId: String): Result<UserResponse> = safeApiCall {
        apiService.checkUserExists(externalId)
    }

    /**
     * 2️⃣ Create a new user (Signup)
     */
    suspend fun createUser(request: CreateUserRequest): Result<UserResponse> = safeApiCall {
        apiService.createUser(request)
    }

    suspend fun updateUserCard(request: UpdateUserCardRequest): Result<UserResponse> = safeApiCall {
        apiService.updateUserCard(request)
    }

    suspend fun updateUser(userId: String, request: UpdateUserRequest): Result<UserResponse> {
        return safeApiCall {
            apiService.updateUser(userId, request)
        }
    }

    suspend fun updateUserLocation(userId: String, request: UpdateUserLocationRequest): Result<UserResponse> {
        return safeApiCall {
            apiService.updateUserLocation(userId, request)
        }
    }



    suspend fun updateUserFCMToken(userId: String, request: UpdateUserFCMTokenRequest): Result<Unit>{
        return safeApiCall {
            apiService.updateUserFCMToken(userId, request)
        }
    }

    suspend fun updateTermsAndConditions(userId: String): Result<Unit>{
        return safeApiCall {
            apiService.updateTermsAndConditions(userId)
        }
    }

    suspend fun deleteUser(userId: String, request: DeleteUserRequest): Result<Unit>{
        return safeApiCall {
            apiService.deleteUser(userId, request)
        }
    }

    /** GOOGLE MAPS API **/
    suspend fun getLocationFromAddress(address: String, apiKey: String): Result<LatLng> {
        return try {
            val response = safeApiCall {
                googleApiService.getLocationFromAddress(address, apiKey)
            }

            response.map { googleResponse ->
                val location = googleResponse.results.firstOrNull()?.geometry?.location
                    ?: throw Exception("No location found in Google Maps response")

                LatLng(location.lat, location.lng)
            }
        } catch (e: Exception) {
            Log.e("RemoteDataSource", "Error fetching location: ${e.localizedMessage}")
            Result.failure(e)
        }
    }



    /** Generic API Call Wrapper to Handle Errors **/
    private suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    response.body()?.let { Result.success(it) }
                        ?: Result.failure(Exception("Empty response body"))
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    Result.failure(HttpException(response).apply { initCause(Exception(errorMessage)) })
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            errorBody?.let {
                org.json.JSONObject(it).optString("message", "Unknown error occurred")
            } ?: "Unknown error occurred"
        } catch (e: Exception) {
            "Error parsing response"
        }
    }

    suspend fun getUser(userId: String?, externalId: String?): Result<UserResponse> {
        return safeApiCall {
            apiService.getUser(userId, externalId)
        }
    }

    suspend fun checkUser(externalId: String): Result<UserResponse> {
        return safeApiCall {
            apiService.getUser(externalId)
        }
    }
    suspend fun getAutocompleteResults(request: AutocompleteRequest, apiKey:String ): Result<AutocompleteResponse> {
        val fullUrl = "https://places.googleapis.com/v1/places:autocomplete" // ✅ Correct URL

        return safeApiCall<AutocompleteResponse> { placesApiService.getAutocompleteResults(request = request, apiKey =  apiKey, fullUrl = fullUrl) }
    }


    suspend fun getAddressFromCoordinates(
        @Query("latlng") latlng: String, // Format: "latitude,longitude"
        @Query("key") apiKey: String
    ): Result<GeocodingResponse>{
        return safeApiCall {
            googleApiService.getAddressFromCoordinates(latlng, apiKey)
        }
    }

    suspend fun getDetailsFromPlaceId(placeId: String, apiKey:String): Result<PlaceDetailsResponse>{
        return  safeApiCall {
            placesApiService.getPlaceDetails(placeId = placeId, apiKey = apiKey)
        }
    }
}
