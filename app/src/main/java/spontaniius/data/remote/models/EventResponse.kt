package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName
import spontaniius.domain.models.Event

data class EventResponse(
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("card_ids") val cardIds: List<Int>,
    @SerializedName("event_title") val eventTitle: String,
    @SerializedName("event_text") val eventText: String?,
    @SerializedName("gender_restrict") val genderRestrict: String?,
    @SerializedName("street_address") val streetAddress: List<Double>?, // Maps directly to JSON array
    @SerializedName("icon") val icon: String?,
    @SerializedName("max_radius") val maxRadius: Int?,
    @SerializedName("event_starts") val eventStarts: String?,
    @SerializedName("event_ends") val eventEnds: String?,
    @SerializedName("distance") val distance: Double? // Matches JSON field
) {
    /**
     * Converts API response to domain model (Event)
     */
    fun toDomain(): Event {
        return Event(
            eventId = eventId,
            ownerId = ownerId,
            title = eventTitle,
            description = eventText,
            gender = genderRestrict ?: "Any",
            address = "Unknown Location",  // No explicit address field in JSON
            icon = icon ?: "default_icon",
            startTime = eventStarts ?: "Unknown Start Time",
            endTime = eventEnds ?: "Unknown End Time",
            latitude = streetAddress?.getOrNull(1) ?: 0.0,  // JSON format: [longitude, latitude]
            longitude = streetAddress?.getOrNull(0) ?: 0.0, // Swap order to match lat/lng
            invitation = maxRadius ?: 1000,
            cardIds = cardIds,
            distance = distance
        )
    }
}
