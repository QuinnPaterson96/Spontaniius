package com.example.spontaniius.data

import android.graphics.Bitmap

data class EventEntity(
    val title: String,
    val description: String,
    val icon: Bitmap,
    val startTime: Long,
    val endTime: Long,
    val location: Any?,
    val gender: Any?,
    val invitation: Any?
) {

}