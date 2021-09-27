package com.company.elverano.data.openWeather

import androidx.room.withTransaction
import com.company.elverano.api.OpenWeatherApi
import com.company.elverano.utils.ResultEvent
import com.company.elverano.utils.networkBoundResource
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWeatherRepository @Inject constructor(
    private val api: OpenWeatherApi,
    private val database: OpenWeatherDatabase,
) {
    private val dao = database.openWeatherDao()
    val resultChannel = Channel<ResultEvent>()

    fun getWeather(lat: Double, lon: Double) = networkBoundResource(
        query = {
            dao.getWeather()
        },
        fetch = {
            delay(2000)
            api.getWeather(latitude = lat, longitude = lon)
        },
        saveFetchResult = { weather ->
            database.withTransaction {
                weather.request {
                    when (it) {
                        is ApiResponse.Success -> {
                            GlobalScope.launch {
                                dao.deleteWeather(it.data)
                                dao.insertWeather(it.data)
                            }
                        }

                        is ApiResponse.Failure.Exception -> {
                            when (it.exception) {
                                is UnknownHostException -> {
                                    GlobalScope.launch {
                                        val msg = "No Item's found\nNo Internet Connection!"
                                        resultChannel.send(ResultEvent.Error(msg))
                                    }
                                }
                                else -> {
                                    GlobalScope.launch {
                                        val msg =
                                            "No Item's found\nException: ${it.exception.message}"
                                        resultChannel.send(ResultEvent.Error(msg))
                                    }
                                    throw it.exception
                                }
                            }
                        }

                        is ApiResponse.Failure.Error ->{
                            GlobalScope.launch {
                                val msg = "No Item's found\nError " + it.statusCode.code
                                resultChannel.send(ResultEvent.Error(msg))
                            }
                        }
                    }
                }

            }

        }
    )
}