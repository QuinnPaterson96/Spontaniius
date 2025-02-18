package spontaniius.data.repository

import spontaniius.data.remote.api.ApiService
import spontaniius.data.remote.models.CardCreateRequest
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val apiService: ApiService // ✅ Inject Retrofit API
) {

    suspend fun createCard(userId: Int?, name: String?, backgroundId: Int): Result<Int> {
        if (userId == null || name == null) {
            return Result.failure(Exception("Invalid user data"))
        }

        val request = CardCreateRequest(
            user_id = userId,
            card_text = name,
            background = backgroundId.toString(),
            background_address = "",
        )

        return try {
            val response = apiService.createCard(request)

            if (response.isSuccessful) {
                val responseBody = response.body()
                val cardId = (responseBody?.get("card_id") as? Double)?.toInt() // ✅ Extract card_id
                if (cardId != null) {
                    Result.success(cardId) // ✅ Return card ID
                } else {
                    Result.failure(Exception("card_id missing from response"))
                }
            } else {
                Result.failure(Exception("Failed to create card: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
