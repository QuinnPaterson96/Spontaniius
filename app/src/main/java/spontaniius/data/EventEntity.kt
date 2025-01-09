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
    val invitation: Int,
    val cardId: Int
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
        jsonObject.put("cardid", this.cardId)
        return jsonObject
    }
}