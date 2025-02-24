package spontaniius.data.remote.models

import spontaniius.domain.models.Event

data class NearbyEventResponse(
    val eventId: Int,
    val eventTitle: String,
    val eventText: String?,
    val genderRestrict: String?,
    val streetAddress: String?, // Stored as "POINT(longitude latitude)"
    val icon: String,
    val maxRadius: Int,
    val eventStarts: String,
    val eventEnds: String,
    val distance: Double  // ✅ New field for UI sorting & display
) {
    fun toDomain(): Event {
        val (latitude, longitude) = parsePostGISPoint(streetAddress)

        return Event(
            eventId = eventId,
            title = eventTitle,
            description = eventText ?: "No description",
            gender = genderRestrict ?: "Any",
            address = streetAddress ?: "Unknown Location",
            icon = icon,
            startTime = eventStarts,
            endTime = eventEnds,
            latitude = latitude,
            longitude = longitude,
            invitation = maxRadius,
            cardId = -1,  // Placeholder, as NearbyEventResponse doesn't return `cardIds`
            distance = distance  // ✅ Store distance in domain model
        )
    }

    private fun parsePostGISPoint(point: String?): Pair<Double, Double> {
        return try {
            if (point != null && point.startsWith("POINT(") && point.endsWith(")")) {
                val coords = point.removePrefix("POINT(").removeSuffix(")").split(" ")
                val longitude = coords[0].toDouble()
                val latitude = coords[1].toDouble()
                Pair(latitude, longitude)
            } else {
                Pair(0.0, 0.0)
            }
        } catch (e: Exception) {
            Pair(0.0, 0.0)
        }
    }
}
