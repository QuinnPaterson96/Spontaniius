package com.example.spontaniius.data.data_source

import com.example.spontaniius.data.EventEntity
import javax.inject.Inject

class RemoteDataSource @Inject constructor() : DataSource {

    override suspend fun saveEvent(eventEntity: EventEntity) {
//        TODO("Not yet implemented")
    }
}