package spontaniius.data.remote.models

import spontaniius.domain.models.Event

data class EventResponse(
    val eventId: Int,
    val cardIds: List<Int>?,
    val eventTitle: String,
    val eventText: String,
    val genderRestrict: String?,
    val streetAddress: Pair<Double, Double>?, // ✅ Now matches the server (Tuple[float, float])
    val icon: String?,
    val maxRadius: Int?,
    val eventStarts: String?, // ✅ May be nullable
    val eventEnds: String?    // ✅ May be nullable
) {
    /**
     * Converts API response to domain model (Event)
     */
    fun toDomain(): Event {
        return Event(
            eventId = eventId,
            title = eventTitle,
            description = eventText,
            gender = genderRestrict ?: "Any", // Default to "Any" if null
            address = "Unknown Location",  // No longer stores PostGIS POINT
            icon = icon ?: "default_icon", // Default icon if null
            startTime = eventStarts ?: "Unknown Start Time",
            endTime = eventEnds ?: "Unknown End Time",
            latitude = streetAddress?.first ?: 0.0,  // Use first value from tuple
            longitude = streetAddress?.second ?: 0.0, // Use second value from tuple
            invitation = maxRadius ?: 1000, // Default invitation radius
            cardId = cardIds?.firstOrNull() ?: -1 // Use first card ID, or -1 if none exist
        )
    }
}
