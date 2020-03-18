package com.example.spontaniius.data.data_source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spontaniius.data.EventEntity

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    //    TODO: Change when we have more solid ways of parsing location data so that we don't load whole database into memory
    @Query("SELECT * FROM events")
    fun getEvents(): LiveData<List<EventEntity>>

}