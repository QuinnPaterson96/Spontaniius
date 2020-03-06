package com.example.spontaniius.data

import com.example.spontaniius.data.data_source.LocalDataSource
import com.example.spontaniius.data.data_source.RemoteDataSource

class DefaultRepository : Repository {

    val localDataSource = LocalDataSource()
    val remoteDataSource = RemoteDataSource()

    override suspend fun saveEvent(eventEntity: EventEntity) {
        TODO("Not yet implemented")
    }
}