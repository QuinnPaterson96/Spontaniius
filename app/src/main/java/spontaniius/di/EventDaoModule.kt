package spontaniius.di

import android.content.Context
import spontaniius.data.data_source.local.EventDao
import spontaniius.data.data_source.local.EventDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventDaoModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideDao(context: Context): EventDao {
        return EventDatabase.getDatabase(context).eventDao()
    }
}