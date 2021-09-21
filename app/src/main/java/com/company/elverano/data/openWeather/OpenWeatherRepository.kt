package com.company.elverano.data.openWeather

import com.company.elverano.api.OpenWeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWeatherRepository @Inject constructor(private val openWeatherApi: OpenWeatherApi) {
    suspend fun getWeatherResponse(lat: Double,lon:Double) = openWeatherApi.getWeather(latitude = lat, longitude = lon)


}