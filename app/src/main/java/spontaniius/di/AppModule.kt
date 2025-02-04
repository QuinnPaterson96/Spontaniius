package spontaniius.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import spontaniius.data.repository.AuthRepository
import spontaniius.data.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository() // Ensure `UserRepository` has a no-arg constructor or `@Inject constructor()`
    }

    @Provides
    @Singleton
    fun provideVolleySingleton(context: Context): VolleySingleton {
        return VolleySingleton.getInstance(context)
    }


    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context): AuthRepository {
        return AuthRepository(context)
    }
}
