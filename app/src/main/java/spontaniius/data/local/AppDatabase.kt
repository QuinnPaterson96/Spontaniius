package spontaniius.data.local

import spontaniius.data.local.dao.UserDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import spontaniius.data.Converters
import spontaniius.data.EventEntity
import spontaniius.data.data_source.local.EventDao
import spontaniius.data.local.dao.CardDao
import spontaniius.data.local.entities.CardEntity
import spontaniius.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class, CardEntity::class, EventEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cardDao(): CardDao
    abstract fun eventDao(): EventDao
}
