package com.company.elverano.ui.search

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.elverano.R
import com.company.elverano.data.openWeather.OpenWeatherRepository
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.data.positionStack.PositionStackRepository
import com.company.elverano.utils.DummyData
import com.company.elverano.utils.ResultEvent
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.reflect.InvocationTargetException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val positionStackRepository: PositionStackRepository,
    private val openWeatherRepository: OpenWeatherRepository
) : ViewModel() {
    private var searchJob: Job? = null
    private var couritineJob: Job? = null
    var currentError = MutableLiveData<String>()
    var weatherList = Array(2) { DummyData.dummy_wroclaw; DummyData.dummy_krakow }

    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()

    private var _weatherResponse  = MutableLiveData<OpenWeatherResponse>()
    val  weatherResponse: LiveData<OpenWeatherResponse> get() = _weatherResponse

    init {
        viewModelScope.launch {
            _weatherResponse.value =  openWeatherRepository.getWeatherFromDB()
        }
    }

    fun searchLocation(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {

            //Old weather is added to list. Two item's of search history  (not current weather)  will be displayed in Fragment
            openWeatherRepository.getWeatherFromDB()?.let {
                val x = weatherList[0]
                x.let {
                    weatherList[1] = x
                }
                weatherList[0]= it
            }

            val response = positionStackRepository.getLocationFromAPI(query)
            response.request { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        viewModelScope.launch {
                            positionStackRepository.deletePositionFromDB()
                            positionStackRepository.insertPositionToDB(response.data)

                            response?.data?.let {
                                val data = it.data
                                if (data != null) {
                                    if (data.size > 0) {
                                        val item = data[0]
                                        item?.let { item ->
                                            searchWeather(
                                                lat = item.latitude,
                                                lon = item.longitude,
                                                name = item.name,
                                                country = item.country
                                            )
                                        }

                                    } else {
                                        viewModelScope.launch {
                                            val msg = "No Item's found"
                                            resultChannel.send(ResultEvent.Error(msg))
                                            currentError.value = msg
                                        }
                                    }
                                }
                            }
                        }


                    }
                    is ApiResponse.Failure.Error -> {
                        viewModelScope.launch {
                            val msg = "No Item's found\nError " + response.statusCode.code
                            resultChannel.send(ResultEvent.Error(msg))
                        }


                    }
                    is ApiResponse.Failure.Exception -> {
                        when (response.exception) {
                            is UnknownHostException -> {
                                viewModelScope.launch {
                                    val msg = "No Item's found\nNo Internet Connection!"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                            }
                            is InvocationTargetException -> {
                                viewModelScope.launch {
                                    val msg = "No Item's found!"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                            }

                            is SocketTimeoutException -> {
                                viewModelScope.launch {
                                    val msg = "No Item's found!"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                            }
                            else -> {
                                viewModelScope.launch {
                                    val msg =
                                        "No Item's found\nException: ${response.exception.message}"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                                throw response.exception
                            }
                        }


                    }
                }
            }

        }
    }

    private fun searchWeather(lat: Double, lon: Double, name: String, country: String) {
        couritineJob?.cancel()
        couritineJob = viewModelScope.launch {
            openWeatherRepository.getWeatherFromAPI(lon = lon, lat = lat).request {
                when (it) {
                    is ApiResponse.Success -> {
                        viewModelScope.launch {
                            val item = it.data
                            openWeatherRepository.deleteWeatherFromDatabase()
                            openWeatherRepository.insertWeatherToDatabase(item)
                            resultChannel.send(ResultEvent.Success)
                        }

                    }

                    is ApiResponse.Failure.Error -> {
                        viewModelScope.launch {
                            val msg = "No Item's found\nError " + it.statusCode.code
                            resultChannel.send(ResultEvent.Error(msg))
                        }

                    }

                    is ApiResponse.Failure.Exception -> {
                        when (it.exception) {
                            is UnknownHostException -> {
                                viewModelScope.launch {
                                    val msg = "No Item's found\nNo Internet Connection!"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                            }
                            else -> {
                                viewModelScope.launch {
                                    val msg =
                                        "No Item's found\nException: ${it.exception.message}"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                                throw it.exception
                            }
                        }

                    }

                }
            }
        }
    }
}