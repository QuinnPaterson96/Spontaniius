package com.example.spontaniius.data

import android.graphics.Bitmap

interface Repository {

    fun createEvent(
        title: String,
        description: String,
        icon: Bitmap,
        startTime: Long,
        endTime: Long,
        location: Any?,
        gender: Any?,
        invitation: Any?)
    //        TODO("add Functions")
}