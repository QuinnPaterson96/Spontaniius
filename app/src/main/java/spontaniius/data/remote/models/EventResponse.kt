package spontaniius.data.remote.models

data class EventResponse(
    val event_id: Int,
    val event_starts: String,
    val event_ends: String,
    val card_ids: List<Int>?
)
