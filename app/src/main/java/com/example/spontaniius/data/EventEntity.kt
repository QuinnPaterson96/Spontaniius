package com.example.spontaniius.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "events")
data class EventEntity(
//    val cardIDs: IntArray, TODO: figure it out: Room cannot figure out how to save an int array
    val title: String,
    val description: String,
    val gender: String,
    val address: String,
    val icon: String,
    val startTime: Long,
    val endTime: Long,
    val invitation: Int
) {
    @PrimaryKey(autoGenerate = true)
    var eventID: Int = 0
/*
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventEntity) return false

        if (!cardIDs.contentEquals(other.cardIDs)) return false
        if (eventID != other.eventID) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (gender != other.gender) return false
        if (address != other.address) return false
        if (icon != other.icon) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (invitation != other.invitation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cardIDs.contentHashCode()
        result = 31 * result + eventID
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + gender.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + invitation
        return result
    }*/
}