package com.company.elverano.api

import com.company.elverano.BuildConfig
import com.company.elverano.data.OpenWeather
import com.company.elverano.data.OpenWeatherRepository
import com.company.elverano.data.OpenWeatherResponse
import com.company.elverano.ui.main.MainViewModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface OpenWeatherApi {

    companion object{
        const val BASE_URL="https://api.openweathermap.org/data/2.5/"
        const val CLIENT_ID= BuildConfig.OPEN_WEATHER_ACCESS_KEY
        const val CurrentWeather = "find"
        const val ListWeather = "forecast?"
        const val Daily = "forecast/daily?"
    }





    @GET("$CurrentWeather?appid=$CLIENT_ID&&units=metric")
 suspend fun getWeather(@Query("q") query:String): Response<OpenWeatherResponse>
}