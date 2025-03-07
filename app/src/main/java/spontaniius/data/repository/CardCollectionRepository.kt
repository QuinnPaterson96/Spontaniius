package spontaniius.data.repository

import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CardCollectionRequest
import spontaniius.data.remote.models.CardResponse
import spontaniius.data.remote.models.CardCollectionResponse
import javax.inject.Inject

class CardCollectionRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val userRepository: UserRepository
) {

    suspend fun getUserCardCollections(): Result<List<CardCollectionResponse>> {
        return remoteDataSource.getUserCardCollections(userId = userRepository.getUserId()!!)
    }

    suspend fun createOrUpdateCardCollection(
        userId: String,
        cardIds: List<Int>,
        eventId: Int,
        meetingDate: String
    ): Result<CardCollectionResponse> {
        val request = CardCollectionRequest(userId, cardIds, eventId, meetingDate)
        return remoteDataSource.createOrUpdateCardCollection(request)
    }
}
