package com.company.elverano.di

import com.company.elverano.api.OpenWeatherApi
import com.company.elverano.api.PositionStackApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(PositionStackApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    @Provides
    @Singleton
    fun provideOpenWeatherApi(retrofit: Retrofit): OpenWeatherApi = retrofit.create(OpenWeatherApi::class.java)




    @Provides
    @Singleton
    fun providePositionStackApi(retrofit: Retrofit): PositionStackApi = retrofit.create(PositionStackApi::class.java)
}