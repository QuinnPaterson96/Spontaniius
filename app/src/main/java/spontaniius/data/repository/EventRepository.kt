package spontaniius.data.repository

import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CreateEventRequest
import spontaniius.data.remote.models.EventResponse
import spontaniius.data.remote.models.ExtendEventRequest
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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

    suspend fun extendEvent(eventId: Int, currentEndTime: String): Result<EventResponse> {
        val newEndTime = ZonedDateTime.parse(
            currentEndTime, DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm:ssz")
        ).plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))

        val request = ExtendEventRequest(event_end = newEndTime)
        return remoteDataSource.extendEvent(eventId, request)
    }

    suspend fun endEvent(eventId: Int): Result<EventResponse> {
        return remoteDataSource.endEvent(eventId)
    }
}
