package spontaniius.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import spontaniius.data.local.AppDatabase
import spontaniius.data.local.dao.UserDao
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.api.ApiService
import spontaniius.data.remote.api.GoogleApiService
import spontaniius.data.repository.AuthRepository
import spontaniius.data.repository.CardRepository
import spontaniius.data.repository.EventRepository
import spontaniius.data.repository.LocationRepository
import spontaniius.data.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val BASE_URL = "https://fgvdpt3hht.us-east-1.awsapprunner.com"
    private const val BASE_URL_GOOGLE = "https://maps.googleapis.com/"

    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideCardRepository(remoteDataSource: RemoteDataSource, userRepository: UserRepository): CardRepository {
        return CardRepository(remoteDataSource = remoteDataSource, userRepository=userRepository) // ✅ Pass ApiService to CardRepository
    }

    @Provides
    @Singleton
    fun provideUserRepository(remoteDataSource: RemoteDataSource, userDao: UserDao): UserRepository {
        return UserRepository(remoteDataSource, userDao = userDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(@ApplicationContext context: Context, remoteDataSource: RemoteDataSource): AuthRepository {
        return AuthRepository(context, remoteDataSource )
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_GOOGLE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleApiService(googleRetrofit: Retrofit): GoogleApiService {
        return googleRetrofit.create(GoogleApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        appApiService: ApiService,
        googleApiService: GoogleApiService
    ): RemoteDataSource {
        return RemoteDataSource(appApiService, googleApiService)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(@ApplicationContext context: Context, fusedLocationProviderClient: FusedLocationProviderClient, remoteDataSource: RemoteDataSource): LocationRepository{
        return LocationRepository(context, fusedLocationProviderClient, remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideEventRepository(remoteDataSource: RemoteDataSource): EventRepository{
        return EventRepository(remoteDataSource)
    }

}
