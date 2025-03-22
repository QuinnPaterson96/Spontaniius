package spontaniius.data.remote.models
import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime
import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

// Custom adapter to handle ZonedDateTime serialization & deserialization
class ZonedDateTimeAdapter : JsonDeserializer<ZonedDateTime>, JsonSerializer<ZonedDateTime> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ZonedDateTime {
        return ZonedDateTime.parse(json?.asString) // Converts string to ZonedDateTime
    }

    override fun serialize(src: ZonedDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString()) // Converts ZonedDateTime to string
    }
}

// Use @JsonAdapter to apply the custom adapter to the meetingDate field
data class CardCollectionResponse(
    @SerializedName("collection_id") val collectionId: Int,
    @SerializedName("user_id") val userId: String,
    @SerializedName("card_ids") val cardIds: List<Int>?,
    @SerializedName("event_id") val eventId: Int,
    @JsonAdapter(ZonedDateTimeAdapter::class) // Apply the custom adapter
    @SerializedName("meeting_date") val meetingDate: ZonedDateTime
)