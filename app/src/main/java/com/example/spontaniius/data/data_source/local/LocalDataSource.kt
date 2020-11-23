package com.example.spontaniius.data.data_source.local

import com.example.spontaniius.data.EventEntity
import com.example.spontaniius.data.data_source.DataSource
import javax.inject.Inject

/**
 * this is the local data source designed to access the database built by the room persistence library
 * Documentation for the library and how it works is found here
 * https://developer.android.com/topic/libraries/architecture/room
 *
 */
class LocalDataSource @Inject constructor(private val eventDao: EventDao) : DataSource {

    override suspend fun saveEvent(eventEntity: EventEntity) {
        eventDao.insertEvent(eventEntity)
    }
}