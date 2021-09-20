package com.company.elverano.data

import com.company.elverano.api.OpenWeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWeatherRepository @Inject constructor(private val openWeatherApi: OpenWeatherApi) {
    suspend fun getWeatherResponse(query:String) = openWeatherApi.getWeather(query)


}