package spontaniius.data.repository

import spontaniius.data.EventEntity

interface Repository {

    suspend fun saveEvent(eventEntity: EventEntity)
}