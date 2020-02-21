package com.example.spontaniius.data

import com.example.spontaniius.data.data_source.LocalDataSource
import com.example.spontaniius.data.data_source.RemoteDataSource

class DefaultRepository : Repository{

    val localDataSource = LocalDataSource()
    val remoteDataSource = RemoteDataSource()

//        TODO("Not yet implemented")
}