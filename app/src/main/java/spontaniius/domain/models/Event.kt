package spontaniius.domain.models

import androidx.room.Entity

import org.json.JSONObject


data class Event(
    val eventId: Int,
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
)