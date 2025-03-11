package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class JoinEventRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("card_id") val cardId: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("meeting_date") val meetingDate: String
)
