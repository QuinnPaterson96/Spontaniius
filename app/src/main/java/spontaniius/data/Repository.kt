package spontaniius.data

interface Repository {

    suspend fun saveEvent(eventEntity: EventEntity)
}