package spontaniius.data.data_source

import spontaniius.data.EventEntity

interface DataSource {

    suspend fun saveEvent(eventEntity: EventEntity)

}