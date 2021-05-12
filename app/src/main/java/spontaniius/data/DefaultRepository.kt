package spontaniius.data

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import spontaniius.data.data_source.local.LocalDataSource
import spontaniius.data.data_source.remote.RemoteDataSource

class DefaultRepository constructor(
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