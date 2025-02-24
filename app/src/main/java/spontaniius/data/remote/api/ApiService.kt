package spontaniius.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import spontaniius.data.remote.models.*

interface ApiService {

    /** EVENTS **/
    @POST("events/create")
    suspend fun createEvent(@Body request: CreateEventRequest): Response<EventResponse>

    @PATCH("events/join")
    suspend fun joinEvent(@Body request: JoinEventRequest): Response<Unit>

    @GET("events/nearby")
    suspend fun getNearbyEvents(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("currentTime") currentTime: String,
        @Query("gender") gender: String?
    ): Response<List<NearbyEventResponse>>

    @GET("events/{event_id}")
    suspend fun getEventById(@Path("event_id") eventId: Int): Response<Any>

    @PATCH("events/{event_id}/extend")
    suspend fun extendEvent(
        @Path("event_id") eventId: Int,
        @Body request: ExtendEventRequest
    ): Response<EventResponse>

    @PATCH("events/{event_id}/end")
    suspend fun endEvent(
        @Path("event_id") eventId: Int
    ): Response<EventResponse>

    @PATCH("events/{event_id}/update_cards")
    suspend fun updateEventCards(
        @Path("event_id") eventId: Int,
        @Body request: UpdateEventCardsRequest
    ): Response<EventResponse>



    /** PROMOTIONS **/
    @POST("promotions/create")
    suspend fun createPromotion(@Body request: PromotionCreateRequest): Response<Map<String, Any>>

    @GET("promotions/all")
    suspend fun getAllPromotions(): Response<List<Map<String, Any>>>

    @GET("promotions/{promotion_id}")
    suspend fun getPromotionById(@Path("promotion_id") promotionId: Int): Response<Map<String, Any>>


    /** CARDS **/
    @POST("cards/create")
    suspend fun createCard(@Body request: CardCreateRequest): Response<Map<String, Any>>

    @GET("cards/{card_id}")
    suspend fun getCardById(@Path("card_id") cardId: Int): Response<Map<String, Any>>


    /** CARD COLLECTIONS **/
    @POST("card-collections/create")
    suspend fun createOrUpdateCardCollection(): Response<Unit>

    @GET("card-collections/{user_id}")
    suspend fun getUserCardCollection(@Path("user_id") userId: String): Response<List<Any>>


    /** REPORTS **/
    @POST("reports/report")
    suspend fun reportUser(): Response<Unit>


    /** USERS **/
    @POST("users/create")
    suspend fun createUser(@Body request: CreateUserRequest): Response<Unit>

    @PUT("users/update_card")
    suspend fun updateUserCard(@Body request: UpdateUserCardRequest): Response<Unit>

    @GET("users/get")
    suspend fun getUser(
        @Query("user_id") userId: String? = null,
        @Query("external_id") externalId: String? = null
    ): Response<UserResponse>


}