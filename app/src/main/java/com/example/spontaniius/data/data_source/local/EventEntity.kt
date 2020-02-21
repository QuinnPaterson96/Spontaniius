package com.example.spontaniius.data.data_source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events_table")
data class EventEntity(
    val address: String,
    val gender: String,
    //    time of event start stored in milliseconds using linux timestamps
    val startTime: Long,
    val duration: Long,
    val title: String,
    val description: String
) {
    @PrimaryKey(autoGenerate = true)
    var eventID: Int = 0
}