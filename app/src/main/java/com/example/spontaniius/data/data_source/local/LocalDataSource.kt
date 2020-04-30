package com.example.spontaniius.data.data_source.local

import com.example.spontaniius.data.EventEntity
import com.example.spontaniius.data.data_source.DataSource
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val eventDao: EventDao) : DataSource {

    override suspend fun saveEvent(eventEntity: EventEntity) {
        eventDao.insertEvent(eventEntity)
    }
}