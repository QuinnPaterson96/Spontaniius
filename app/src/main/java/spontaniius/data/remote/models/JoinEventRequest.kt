package spontaniius.data.remote.models

data class JoinEventRequest(
    val event_id: Int,
    val user_id: String,
    val card_id: Int
)

