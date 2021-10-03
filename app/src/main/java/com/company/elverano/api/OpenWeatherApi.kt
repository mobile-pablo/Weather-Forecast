package com.company.elverano.api

import com.company.elverano.BuildConfig
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.data.positionStack.PositionStackResponse
import com.skydoves.sandwich.ApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Named

interface OpenWeatherApi{

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val CLIENT_ID = BuildConfig.OPEN_WEATHER_ACCESS_KEY
        const val oneCall ="onecall"
    }


    @GET("$oneCall?appid=$CLIENT_ID&exclude=minutely,daily&units=metric")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Call<OpenWeatherResponse?>
}