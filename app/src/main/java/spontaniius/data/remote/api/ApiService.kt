package spontaniius.data.remote.api

import retrofit2.Response
import retrofit2.http.*
import spontaniius.data.remote.models.*
import spontaniius.domain.models.Event
import spontaniius.domain.models.User

interface ApiService {

    /** EVENTS **/
    @POST("events/create")
    suspend fun createEvent(@Body request: CreateEventRequest): Response<EventResponse>

    @PATCH("events/join")
    suspend fun joinEvent(@Body request: JoinEventRequest): Response<EventResponse>

    @POST("events/nearby")
    suspend fun getNearbyEvents(@Body request: FindEventRequest): Response<List<EventResponse>>

    @GET("events/{event_id}")
    suspend fun getEventById(@Path("event_id") eventId: Int): Response<EventResponse>

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
    suspend fun createCard(@Body request: CardCreateRequest): Response<CardResponse>

    @GET("cards/{card_id}")
    suspend fun getCardById(@Path("card_id") cardId: Int): Response<Map<String, Any>>

    @POST("cards/get-cards")
    suspend fun getCardDetails(@Body request: GetCardsRequest): Response<List<CardResponse>>



    /** CARD COLLECTIONS **/
    @POST("card-collections/create")
    suspend fun createOrUpdateCardCollection(createCardCollectionRequest: CardCollectionRequest): Response<CardCollectionResponse>

    @GET("card-collections/{user_id}")
    suspend fun getUserCardCollections(@Path("user_id") userId: String): Response<List<CardCollectionResponse>>


    /** REPORTS **/
    @POST("reports/report")
    suspend fun reportUser(@Body request: ReportRequest): Response<ReportResponse>


    /** USERS **/
    @GET("/users/check-user")
    suspend fun checkUserExists(@Query("external_id") externalId: String): Response<UserResponse>
    /**
     * 2️⃣ Create a new user (Signup)
     */
    @POST("/users/register")
    suspend fun createUser(@Body request: CreateUserRequest): Response<UserResponse>

    @PUT("users/update_card")
    suspend fun updateUserCard(@Body request: UpdateUserCardRequest): Response<UserResponse>

    @GET("users/get")
    suspend fun getUser(
        @Query("user_id") userId: String? = null,
        @Query("external_id") externalId: String? = null
    ): Response<UserResponse>

    @GET("users/check-user")
    suspend fun checkUser(
        @Query("user_id") userId: String? = null,
        @Query("external_id") externalId: String? = null
    ): Response<UserResponse>
}