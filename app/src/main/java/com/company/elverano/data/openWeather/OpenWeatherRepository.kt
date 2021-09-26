package com.company.elverano.data.openWeather

import com.company.elverano.api.OpenWeatherApi
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWeatherRepository @Inject constructor(
     private val openWeatherApi: OpenWeatherApi,
     private val openWeatherDatabase: OpenWeatherDatabase
) {
     private val openWeatherDao = openWeatherDatabase.openWeatherDao()

   suspend fun insertWeatherResponse(lat: Double, lon:Double){
        openWeatherApi.getWeather(latitude = lat, longitude = lon).request {
            when(it){
                 is ApiResponse.Success -> {
                     GlobalScope.launch {
                          openWeatherDao.insertWeather(it.data)
                     }
                 }
            }
          }


     }

      fun getWeatherResponse() : Flow<OpenWeatherResponse> = openWeatherDao.getWeather()
}