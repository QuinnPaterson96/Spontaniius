package spontaniius.data.remote.models
import com.google.gson.annotations.SerializedName

data class CreateEventRequest(
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("card_ids") val cardIds: List<Int>,
    @SerializedName("event_title") val eventTitle: String,
    @SerializedName("event_text") val eventText: String,
    @SerializedName("gender_restrict") val genderRestrict: String,
    @SerializedName("street_name") val StreetName: String?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("icon") val icon: String,
    @SerializedName("max_radius") val maxRadius: Int,
    @SerializedName("event_starts") val eventStarts: String,
    @SerializedName("event_ends") val eventEnds: String
)
