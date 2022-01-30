package com.company.elverano.di

import android.app.Application
import androidx.room.Room
import com.company.elverano.api.OpenWeatherApi
import com.company.elverano.api.PositionStackApi
import com.company.elverano.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("positionStackRetrofit")
    fun providerPositionStackRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(PositionStackApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("openWeatherRetrofit")
    fun provideOpenWeatherRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(OpenWeatherApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideOpenWeatherApi(
        @Named("openWeatherRetrofit") openWeatherRetrofit: Retrofit
    ): OpenWeatherApi =
        openWeatherRetrofit.create(OpenWeatherApi::class.java)

    @Provides
    @Singleton
    fun providePositionStackApi(
        @Named("positionStackRetrofit") positionStackRetrofit: Retrofit
    ): PositionStackApi =
        positionStackRetrofit.create(PositionStackApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: AppDatabase.Callback
    ) = Room.databaseBuilder(app, AppDatabase::class.java, "app_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    fun provideHistoryDao(db: AppDatabase) = db.historyWeatherDao()
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

