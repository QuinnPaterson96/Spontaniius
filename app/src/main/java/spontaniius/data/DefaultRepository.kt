package spontaniius.data

import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.data_source.local.LocalDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import spontaniius.data.repository.Repository
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
                localDataSource.saveEvent(eventEntity)
            }
        }
    }
}