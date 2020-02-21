package com.example.spontaniius.data

import com.example.spontaniius.data.data_source.local.LocalDataSource
import com.example.spontaniius.data.data_source.remote.RemoteDataSource

class DefaultRepository : Repository{

    val localDataSource =
        LocalDataSource()
    val remoteDataSource =
        RemoteDataSource()

//        TODO("Not yet implemented")
}