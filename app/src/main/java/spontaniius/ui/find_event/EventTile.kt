package spontaniius.ui.find_event

import android.content.Context
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
    val eventId: Int,
    val latitude: Double,
    val longitude: Double,
    val event: Event,
    val context: Context
) {
    companion object {
        fun fromDomain(event: Event, context: Context): EventTile {
            if (event.description != null){
                return EventTile(
                    imageResource = event.icon.toIntOrNull() ?: DEFAULT_ICON, // Convert icon string to Int if possible
                    title = event.title,
                    description = event.description,
                    distance = event.distance?.toString() ?: "Unknown",
                    timeRemaining = calculateTimeRemaining(event.startTime, event.endTime, context),
                    location = event.address,
                    eventId = event.eventId,
                    latitude = event.latitude,
                    longitude = event.longitude,
                    event = event,
                    context = context
                )

            }else {
                return EventTile(
                    imageResource = event.icon.toIntOrNull()
                        ?: DEFAULT_ICON, // Convert icon string to Int if possible
                    title = event.title,
                    description = "",
                    distance = event.distance?.toString() ?: "Unknown",
                    timeRemaining = calculateTimeRemaining(event.startTime, event.endTime, context),
                    location = event.address,
                    eventId = event.eventId,
                    latitude = event.latitude,
                    longitude = event.longitude,
                    event = event,
                    context = context
                )
            }
        }

        private fun calculateTimeRemaining(startTime: String, endTime: String, context: Context): String {
            val currTime = System.currentTimeMillis() / 1000
            val eventStart = ZonedDateTime.parse(startTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toEpochSecond()
            val eventEnd = ZonedDateTime.parse(endTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toEpochSecond()

            return when {
                currTime < eventStart -> {
                    val minutesToStart = ((eventStart - currTime) / 60).toInt()
                    formatTime(minutesToStart, context.getString(R.string.starts_in_format), context)
                }
                currTime in eventStart..eventEnd -> {
                    val minutesToEnd = ((eventEnd - currTime) / 60).toInt()
                    formatTime(minutesToEnd, context.getString(R.string.ends_in_format), context)
                }
                else -> {
                    context.getString(R.string.event_has_ended)
                }
            }
        }

        private fun formatTime(totalMinutes: Int, formatString: String, context: Context): String {
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            return when {
                hours > 0 && minutes > 0 -> context.getString(R.string.hours_and_minutes, hours, minutes, formatString)
                hours > 0 -> context.getString(R.string.only_hours, hours, formatString)
                else -> context.getString(R.string.only_minutes, minutes, formatString)
            }
        }

        private var DEFAULT_ICON = R.drawable.activity_other // Replace with an actual default icon
    }
}
