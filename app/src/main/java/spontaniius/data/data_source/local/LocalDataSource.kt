package spontaniius.data.data_source.local

import spontaniius.data.EventEntity
import spontaniius.data.data_source.DataSource

/**
 * this is the local data source designed to access the database built by the room persistence library
 * Documentation for the library and how it works is found here
 * https://developer.android.com/topic/libraries/architecture/room
 *
 */
class LocalDataSource constructor(private val eventDao: EventDao) : DataSource {

    override suspend fun saveEvent(eventEntity: EventEntity) {
        eventDao.insertEvent(eventEntity)
    }
}