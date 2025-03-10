package spontaniius.ui.find_event

import com.spontaniius.R
import spontaniius.domain.models.Event
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class EventTile(
    val imageResource: Int,
    val title: String,
    val description: String,
    val distance: String,
    val timeRemaining: String,
    val location: String,
    val eventId: String,
    val latitude: Double,
    val longitude: Double,
    val event: Event
) {
    companion object {
        fun fromDomain(event: Event): EventTile {
            if (event.description != null){
                return EventTile(
                    imageResource = event.icon.toIntOrNull() ?: DEFAULT_ICON, // Convert icon string to Int if possible
                    title = event.title,
                    description = event.description,
                    distance = event.distance?.toString() ?: "Unknown",
                    timeRemaining = calculateTimeRemaining(event.startTime, event.endTime),
                    location = event.address,
                    eventId = event.eventId.toString(),
                    latitude = event.latitude,
                    longitude = event.longitude,
                    event = event
                )

            }else {
                return EventTile(
                    imageResource = event.icon.toIntOrNull()
                        ?: DEFAULT_ICON, // Convert icon string to Int if possible
                    title = event.title,
                    description = "",
                    distance = event.distance?.toString() ?: "Unknown",
                    timeRemaining = calculateTimeRemaining(event.startTime, event.endTime),
                    location = event.address,
                    eventId = event.eventId.toString(),
                    latitude = event.latitude,
                    longitude = event.longitude,
                    event = event
                )
            }
        }

        private fun calculateTimeRemaining(startTime: String, endTime: String): String {
            val currTime = System.currentTimeMillis() / 1000
            val eventStart = ZonedDateTime.parse(startTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toEpochSecond()
            val eventEnd = ZonedDateTime.parse(endTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toEpochSecond()

            val minutesFromStart = ((eventStart - currTime) / 60).toInt()
            val minutesFromEnd = ((eventEnd - currTime) / 60).toInt()

            return when {
                minutesFromStart < 0 -> "ends in $minutesFromEnd mins"
                else -> "starts in $minutesFromStart mins"
            }
        }

        private var DEFAULT_ICON = R.drawable.activity_other // Replace with an actual default icon
    }
}
