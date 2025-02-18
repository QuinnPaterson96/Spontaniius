package spontaniius.data.repository

import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CreateEventRequest
import spontaniius.data.remote.models.EventResponse
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    /**
     * Calls the API to create an event.
     */
    suspend fun createEvent(request: CreateEventRequest): Result<EventResponse> {
        return remoteDataSource.createEvent(request)
    }
}
