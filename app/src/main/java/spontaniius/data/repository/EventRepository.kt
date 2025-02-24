package spontaniius.data.repository

import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CreateEventRequest
import spontaniius.data.remote.models.EventResponse
import spontaniius.data.remote.models.ExtendEventRequest
import spontaniius.domain.models.Event
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    /**
     * Calls the API to create an event.
     */
    suspend fun getNearbyEvents(lat: Double, lng: Double, gender: String?): Result<List<Event>> {
        return remoteDataSource.getNearbyEvents(lat, lng, gender).map { nearbyEventResponses ->
            nearbyEventResponses.map { it.toDomain() }
        }
    }

    suspend fun createEvent(request: CreateEventRequest): Result<EventResponse> {
        return remoteDataSource.createEvent(request)
    }

    suspend fun extendEvent15Mins(eventId: Int, currentEndTime: String): Result<EventResponse> {
        val newEndTime = ZonedDateTime.parse(
            currentEndTime, DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm:ssz")
        ).plusMinutes(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))

        val request = ExtendEventRequest(event_end = newEndTime)
        return remoteDataSource.extendEvent(eventId, request)
    }

    suspend fun endEvent(eventId: Int): Result<EventResponse> {
        return remoteDataSource.endEvent(eventId)
    }

    suspend fun fetchEventDetails(eventId: Int): Result<Event> {
        return remoteDataSource.getEventById(eventId).mapCatching { response ->
            (response as EventResponse).toDomain() // ✅ Convert API response to domain model
        }
    }


    fun getGoogleMapsUrl(streetAddress: String?): String? {
        if (streetAddress.isNullOrEmpty()) return null
        return "https://www.google.com/maps/dir/?api=1&destination=$streetAddress"
    }
}
