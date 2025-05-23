package spontaniius.domain.models

import spontaniius.data.EventEntity


data class Event(
    val eventId: Int,
    val ownerId: String,
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
    val distance: Double? = null
){
    fun toEntity(): EventEntity {
        return EventEntity(
            title = this.title,
            ownerId = ownerId,
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
            distance = distance
        )
    }
}