package com.example.spontaniius.data

data class EventEntity(
    val title: String,
    val description: String,
    val icon: String,
    val startTime: Long,
    val endTime: Long,
    val location: Any?,
    val gender: String,
    val invitation: Int
) {

}