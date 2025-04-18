package spontaniius.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.json.JSONObject
import spontaniius.domain.models.Event


@Entity(tableName = "events")
@TypeConverters(Converters::class)
data class EventEntity(

    val title: String,
    val description: String?,
    val gender: String,
    val streetName: String?,
    val icon: String,
    val startTime: String,
    val endTime: String,
    val latitude: Double,
    val longitude: Double,
    val invitation: Int,
    val cardIds: List<Int>,
    val ownerId: String,
    val distance: Double?
) {


    @PrimaryKey(autoGenerate = true)
    var eventID: Int = 0

    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("eventtitle", this.title)
        jsonObject.put("eventtext", this.description)
        jsonObject.put("genderrestrict", this.gender)
        jsonObject.put("icon", this.icon)
        jsonObject.put("streetaddress", "${this.latitude},${this.longitude}")
        jsonObject.put("maxradius", this.invitation)
        jsonObject.put("eventstarts", this.startTime)
        jsonObject.put("eventends", this.endTime)
        jsonObject.put("cardid", this.cardIds)
        return jsonObject
    }

    fun toDomainModel(): Event {
        return Event(
            eventId = this.eventID,
            ownerId = this.ownerId,
            title = this.title,
            description = this.description,
            gender = this.gender,
            streetName = this.streetName,
            icon = this.icon,
            startTime = this.startTime,
            endTime = this.endTime,
            latitude = this.latitude,
            longitude = this.longitude,
            invitation = this.invitation,
            cardIds = this.cardIds,
            distance = distance // This field is nullable in Event domain model
        )
    }
}