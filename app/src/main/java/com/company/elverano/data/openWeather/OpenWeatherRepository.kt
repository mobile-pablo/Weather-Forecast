package com.company.elverano.data.openWeather

import com.company.elverano.api.OpenWeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWeatherRepository @Inject constructor(
    private val api: OpenWeatherApi,
    private val database: OpenWeatherDatabase,
) {
    private val dao = database.openWeatherDao()

  fun getWeatherFromAPI(lat: Double, lon: Double)= api.getWeather(latitude = lat, longitude = lon)

   suspend fun insertWeatherToDatabase(data: OpenWeatherResponse){
        dao.insertWeather(data)
    }

  suspend  fun getWeatherFromDB() = dao.getWeather()
  suspend  fun deleteWeatherFromDatabase() {
       dao.deleteWeather()
    }
}