package com.company.elverano.data.openWeather

import androidx.room.withTransaction
import com.company.elverano.api.OpenWeatherApi
import com.company.elverano.data.openWeather.query.OpenWeatherQuery
import com.company.elverano.utils.networkBoundResource
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWeatherRepository @Inject constructor(
    private val openWeatherApi: OpenWeatherApi,
    private val openWeatherDatabase: OpenWeatherDatabase
) {
    private val openWeatherDao = openWeatherDatabase.openWeatherDao()
    private val openWeatherQueryDao = openWeatherDatabase.openWeatherQueryDao()

    fun getQuery(): Flow<OpenWeatherQuery> = openWeatherQueryDao.getQuery()

    suspend fun insertQuery(local_query: String) = openWeatherQueryDao.insertQuery(OpenWeatherQuery(query = local_query))

    fun getWeather(lat: Double, lon: Double) = networkBoundResource(
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
                    when (it) {
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