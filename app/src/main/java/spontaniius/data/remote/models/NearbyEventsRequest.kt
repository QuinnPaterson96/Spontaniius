package spontaniius.data.remote.models

data class NearbyEventsRequest(
    val lat: Double,
    val lng: Double,
    val current_time: String,
    val gender: String = "Any"
)

