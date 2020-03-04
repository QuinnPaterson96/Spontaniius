package com.example.spontaniius.data

import android.graphics.Bitmap
import com.example.spontaniius.data.data_source.LocalDataSource
import com.example.spontaniius.data.data_source.RemoteDataSource

class DefaultRepository : Repository{

    val localDataSource = LocalDataSource()
    val remoteDataSource = RemoteDataSource()

    override fun createEvent(
        title: String,
        description: String,
        icon: Bitmap,
        startTime: Long,
        endTime: Long,
        location: Any?,
        gender: Any?,
        invitation: Any?
    ) {
        TODO("Not yet implemented")
    }
}