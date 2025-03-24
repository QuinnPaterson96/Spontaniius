package spontaniius.data.repository

import spontaniius.data.local.dao.CardDao
import spontaniius.data.local.entities.CardEntity
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CardCreateRequest
import spontaniius.data.remote.models.CardResponse
import spontaniius.domain.models.Card
import javax.inject.Inject

class CardRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource, // ✅ Use RemoteDataSource instead
    private val userRepository: UserRepository,
    private val cardDao: CardDao
) {
    suspend fun getCardDetails(cardIds: List<Int>): List<Card> {
        return try {
            // ✅ Get cached cards & ensure proper List<Card> return type
            val cachedCards: List<Card> = cardDao.getCardsByIds(cardIds)
                .map { it.toDomain() } // ✅ Ensure non-null conversion

            val cachedCardIds: Set<Int> = cachedCards.map { it.id }.toSet() // ✅ Fast lookup

            // ✅ Identify missing card IDs
            val missingCardIds: List<Int> = cardIds.filter { it !in cachedCardIds }

            if (missingCardIds.isNotEmpty()) {
                // ✅ Fetch remote cards safely (unwraps Result)
                val response: List<CardResponse> = remoteDataSource.getCardDetails(missingCardIds)
                    .getOrElse { emptyList() } // ✅ Extracts value from Result safely

                if (response.isNotEmpty()) {
                    val newCards: List<Card> = response.map { it.toDomain() } // ✅ Convert to domain
                    val newCardEntities: List<CardEntity> = response.map { it.toEntity() } // ✅ Convert to entity

                    cardDao.insertCards(newCardEntities) // ✅ Insert only if new cards exist

                    return (cachedCards + newCards).distinctBy { it.id } // ✅ Merge and remove duplicates
                }
            }

            return cachedCards // ✅ Always return a valid list
        } catch (e: Exception) {
            emptyList<Card>() // ✅ Safe fallback
        }
    }






    suspend fun createCard(card_text: String?, backgroundId: Int): Result<Int> {
        val user = userRepository.getUserDetails()
        if (user?.id == null) {
            return Result.failure(Exception("Invalid user data"))
        }

        val request = CardCreateRequest(
            user_id = user.id,
            user_name = user.name,
            card_text = card_text,
            background = backgroundId.toString(),
            background_address = ""
        )

        return try {
            val response = remoteDataSource.createCard(request) // ✅ Call RemoteDataSource

            response.fold(
                onSuccess = { responseBody ->
                    cardDao.insertCards(listOf(responseBody.toEntity()))
                    val cardId = responseBody.cardId
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
