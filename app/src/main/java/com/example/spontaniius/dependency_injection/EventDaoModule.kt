package com.example.spontaniius.dependency_injection

import android.content.Context
import com.example.spontaniius.data.data_source.local.EventDao
import com.example.spontaniius.data.data_source.local.EventDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object EventDaoModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideDao(context: Context): EventDao {
        return EventDatabase.getDatabase(context).eventDao()
    }
}