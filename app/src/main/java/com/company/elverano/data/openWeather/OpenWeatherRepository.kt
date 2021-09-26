package com.company.elverano.data.openWeather

import androidx.room.withTransaction
import com.company.elverano.api.OpenWeatherApi
import com.company.elverano.utils.networkBoundResource
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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

    fun getWeather(lat: Double, lon:Double) = networkBoundResource(
        query = {
            openWeatherDao.getWeather()
        },
        fetch = {
            delay(2000)
            openWeatherApi.getWeather(latitude = lat, longitude = lon)
        },
        saveFetchResult = { weather ->
            //Transakcje musza dzialac w calosci etc . Jezeli nie to sie cofa wszysytko jesli db zrozumialem
            openWeatherDatabase.withTransaction {
                weather.request {
                    when(it){
                        is ApiResponse.Success -> {
                            GlobalScope.launch {
                                openWeatherDao.deleteWeather(it.data)
                                openWeatherDao.insertWeather(it.data)
                            }
                        }
                    }
                }

            }

        }
    )
}