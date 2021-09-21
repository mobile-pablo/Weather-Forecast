package com.company.elverano.api

import com.company.elverano.BuildConfig
import com.company.elverano.data.openWeather.OpenWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val CLIENT_ID = BuildConfig.OPEN_WEATHER_ACCESS_KEY
        const val CurrentWeather = "find"
        const val ListWeather = "forecast"
        const val Daily = "forecast/daily"
        const val oneCall ="onecall"
    }


    @GET("$oneCall?appid=$CLIENT_ID&exclude=minutely&units=metric")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Response<OpenWeatherResponse>
}