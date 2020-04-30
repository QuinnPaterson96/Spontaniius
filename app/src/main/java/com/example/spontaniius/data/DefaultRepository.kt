package com.example.spontaniius.data

import com.example.spontaniius.data.data_source.remote.RemoteDataSource
import com.example.spontaniius.data.data_source.local.LocalDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : Repository {

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