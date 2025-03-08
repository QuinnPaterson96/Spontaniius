package spontaniius.data.repository

import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import spontaniius.data.data_source.local.EventDao
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CreateEventRequest
import spontaniius.data.remote.models.EventResponse
import spontaniius.data.remote.models.ExtendEventRequest
import spontaniius.data.remote.models.FindEventRequest
import spontaniius.domain.models.Event
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val userRepository: UserRepository,
    private val eventDao: EventDao
) {
    /**
     * Calls the API to create an event.
     */
    suspend fun getNearbyEvents(lat: Double, lng: Double, gender: String?): Result<List<Event>> {
        val request = FindEventRequest(lat = lat, lng = lng, gender = gender ?: "Any")  // ✅ Use default "Any"
        return remoteDataSource.getNearbyEvents(request).map { nearbyEventResponses ->
            nearbyEventResponses.map { it.toDomain() }
        }
    }

    suspend fun createEvent(title: String,
                            description: String,
                            gender: String,
                            icon: String,
                            event_starts: String,  // ISO 8601 format: YYYY-MM-DDTHH:mm:ssZ
                            event_ends: String,
                            latLng: LatLng,
                            max_radius: Int): Result<EventResponse> {

        val userCardId = userRepository.getUserCardId()
        val userId = userRepository.getUserId()

        val request = CreateEventRequest(
            title = title,
            owner_id = userId!!,
            description = description,
            gender = gender,
            icon = icon,
            event_starts = event_starts,
            event_ends = event_ends,
            latitude = latLng.latitude,
            longitude = latLng.longitude,
            max_radius = max_radius,
            card_id = userCardId
        )
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

    suspend fun saveCreatedEvent(event: Event){
        eventDao.insertEvent(event.toEntity())
    }


    fun getGoogleMapsUrl(streetAddress: String?): String? {
        if (streetAddress.isNullOrEmpty()) return null
        return "https://www.google.com/maps/dir/?api=1&destination=$streetAddress"
    }
}
