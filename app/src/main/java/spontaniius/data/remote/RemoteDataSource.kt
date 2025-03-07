package spontaniius.data.remote

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import spontaniius.data.remote.api.ApiService
import spontaniius.data.remote.api.GoogleApiService
import spontaniius.data.remote.models.*
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService,
    private val googleApiService: GoogleApiService  // Google Maps API

) {

    /** EVENTS **/
    suspend fun createEvent(request: CreateEventRequest): Result<EventResponse> = safeApiCall {
        apiService.createEvent(request)
    }

    suspend fun joinEvent(request: JoinEventRequest): Result<Unit> = safeApiCall {
        apiService.joinEvent(request)
    }

    suspend fun getNearbyEvents(lat: Double, lng: Double, gender: String?): Result<List<NearbyEventResponse>> {
        val currentTime = System.currentTimeMillis().toString()
        return safeApiCall {
            apiService.getNearbyEvents(lat, lng, currentTime, gender)
        }
    }


    suspend fun getEventById(eventId: Int): Result<Any> = safeApiCall {
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
    suspend fun createCard(request: CardCreateRequest): Result<Map<String, Any>> = safeApiCall {
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
}
