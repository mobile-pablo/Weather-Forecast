package com.company.elverano.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.company.elverano.api.OpenWeatherApi
import com.company.elverano.api.PositionStackApi
import com.company.elverano.data.openWeather.OpenWeatherDao
import com.company.elverano.data.openWeather.OpenWeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
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
    fun provideOpenWeatherApi  ( @Named("openWeatherRetrofit") openWeatherRetrofit: Retrofit): OpenWeatherApi = openWeatherRetrofit.create(OpenWeatherApi::class.java)


    @Provides
    @Singleton
    fun providePositionStackApi( @Named("positionStackRetrofit") positionStackRetrofit: Retrofit): PositionStackApi = positionStackRetrofit.create(PositionStackApi::class.java)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): OpenWeatherDatabase {
        return Room.databaseBuilder(
            appContext,
            OpenWeatherDatabase::class.java,
            "open_weather_database"
        ).build()
    }


    // Dao is default singleton  becouse of Room

    @Provides
    fun provideTaskDao(db: OpenWeatherDatabase) = db.openWeatherDao()

}