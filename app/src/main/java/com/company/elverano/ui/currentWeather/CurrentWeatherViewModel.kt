package com.company.elverano.ui.currentWeather


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.elverano.data.openWeather.OpenWeatherRepository
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.data.positionStack.PositionStackRepository
import com.company.elverano.utils.ResultEvent
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject


@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val openWeatherRepository: OpenWeatherRepository,
    private val positionStackRepository: PositionStackRepository
) : ViewModel() {

    var currentWeather = MutableLiveData<OpenWeatherResponse>()
    var currentName = MutableLiveData<String>()
    var currentCountry = MutableLiveData<String>()
    var currentError = MutableLiveData<String>()
    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()
    private var searchJob: Job? = null
    private var couritineJob: Job? = null

    init {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val openWeatherResponse = openWeatherRepository.getWeatherFromDB()
            if (openWeatherResponse == null) {
                searchLocation("Warsaw")
            } else {
                openWeatherResponse?.let {
                    currentWeather.value = it
                }
            }

            positionStackRepository.getLocationFromDatabase()?.data?.let {
                if (it.isNotEmpty()) {
                    currentName.value = it[0].name
                    currentCountry.value = it[0].country_code
                }
            }


        }

    }

    private fun searchLocation(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val response = positionStackRepository.getLocationFromAPI(query)
            response?.request { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        viewModelScope.launch {


                            response.data?.let {
                                positionStackRepository.deletePositionFromDB()
                                positionStackRepository.insertPositionToDB(it)
                                val list = it.data
                             list?.let {
                                 if (it.isNotEmpty()) {
                                     val item = it[0]

                                     item?.let { item ->
                                         searchWeather(
                                             lat = item.latitude,
                                             lon = item.longitude,
                                             name = item.name,
                                             country = item.country
                                         )
                                     }

                                     Log.d(
                                         "CurrentWeather",
                                         "Item : ${item.latitude} , ${item.longitude} , ${item.name}"
                                     )

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
            val response = openWeatherRepository.getWeatherFromAPI(lon = lon, lat = lat)
            response?.request { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        viewModelScope.launch {
                        response?.data?.let {
                            openWeatherRepository.deleteWeatherFromDatabase()
                            openWeatherRepository.insertWeatherToDatabase(it)

                            currentWeather.value = response.data
                            currentName.value = name
                            currentCountry.value = country
                            resultChannel.send(ResultEvent.Success)
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
}