package com.example.spontaniius.data

import com.example.spontaniius.data.data_source.LocalDataSource
import com.example.spontaniius.data.data_source.RemoteDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DefaultRepository : Repository {

    private val localDataSource = LocalDataSource()
    private val remoteDataSource = RemoteDataSource()

    override suspend fun saveEvent(eventEntity: EventEntity) {
        coroutineScope {
            launch {
                localDataSource.saveEvent(eventEntity)
            }
            launch {
                remoteDataSource.saveEvent(eventEntity)
            }
        }
    }
}