package spontaniius.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject


@Entity(tableName = "events")
data class EventEntity(
    val title: String,
    val description: String,
    val gender: String,
    val address: String,
    val icon: String,
    val startTime: String,
    val endTime: String,
    val latitude: Double,
    val longitude: Double,
    val invitation: Int
) {


    @PrimaryKey(autoGenerate = true)
    var eventID: Int = 0

    fun toJSON(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("eventTitle", this.title)
        jsonObject.put("eventText", this.description)
        jsonObject.put("genderRestrict", this.gender)
        jsonObject.put("icon", this.icon)
//        TODO: test actual addresses
        jsonObject.put("streetAddress", "(${this.latitude},${this.longitude})")
        jsonObject.put("maxRadius", this.invitation)
        jsonObject.put("eventStarts", this.startTime)
        jsonObject.put("eventEnds", this.endTime)
        return jsonObject
    }
}