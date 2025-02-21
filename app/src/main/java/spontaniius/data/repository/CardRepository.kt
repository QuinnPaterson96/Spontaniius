package spontaniius.data.repository

import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CardCreateRequest
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource, // ✅ Use RemoteDataSource instead
    private val userRepository: UserRepository
) {

    suspend fun createCard(card_text: String?, backgroundId: Int): Result<Int> {
        val userId = userRepository.getUserId()
        if (userId == null) {
            return Result.failure(Exception("Invalid user data"))
        }

        val request = CardCreateRequest(
            user_id = userId,
            card_text = card_text,
            background = backgroundId.toString(),
            background_address = ""
        )

        return try {
            val response = remoteDataSource.createCard(request) // ✅ Call RemoteDataSource

            response.fold(
                onSuccess = { responseBody ->
                    val cardId = (responseBody["card_id"] as? Double)?.toInt()
                    if (cardId != null) {
                        Result.success(cardId) // ✅ Return extracted card ID
                    } else {
                        Result.failure(Exception("card_id missing from response"))
                    }
                },
                onFailure = { error ->
                    Result.failure(Exception("Failed to create card: ${error.message}"))
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
