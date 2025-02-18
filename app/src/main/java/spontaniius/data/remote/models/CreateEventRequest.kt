package spontaniius.data.remote.models

data class CreateEventRequest(
    val title: String,
    val description: String,
    val gender: String,
    val icon: String,
    val event_starts: String,  // ISO 8601 format: YYYY-MM-DDTHH:mm:ssZ
    val event_ends: String,
    val latitude: Double,
    val longitude: Double,
    val max_radius: Int,
    val card_id: Int
)
