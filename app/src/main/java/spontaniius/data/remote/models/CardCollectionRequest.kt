package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName

data class CardCollectionRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("card_ids") val cardIds: List<Int>,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("meeting_date") val meetingDate: String // ISO format (e.g., "2024-03-01T12:30:00Z")
)
