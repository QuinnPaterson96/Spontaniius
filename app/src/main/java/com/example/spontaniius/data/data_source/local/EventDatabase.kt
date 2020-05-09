package com.example.spontaniius.data.data_source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spontaniius.data.EventEntity

//TODO: Export schema = true for release
@Database(entities = [EventEntity::class], version = 2, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var instance: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            val tempInstance = instance
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val newInstance =
                    Room.databaseBuilder(context, EventDatabase::class.java, "event_database")
                        .fallbackToDestructiveMigration()
                        .build()
//                TODO: remove fallback to destructive Migration for release
                instance = newInstance
                return newInstance
            }
        }
    }
}