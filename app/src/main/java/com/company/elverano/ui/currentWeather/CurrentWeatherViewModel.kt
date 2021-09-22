package com.company.elverano.ui.currentWeather


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.elverano.data.openWeather.OpenWeatherRepository
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.data.positionStack.PositionStackRepository
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject


@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val openWeatherRepository: OpenWeatherRepository,
    private val positionStackRepository: PositionStackRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    var currentWeather = MutableLiveData<OpenWeatherResponse>()
    var currentName = MutableLiveData<String>()
    var currentError = MutableLiveData<String>()
    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()

    init {
        searchLocation(currentQuery.value.toString())
    }

    fun searchLocation(query: String) = viewModelScope.launch {

        currentQuery.value = query
        val request = positionStackRepository.getLocation(query).request { response ->
            when (response) {
                is ApiResponse.Success -> {
                    val data = response.data.data

                    if (data.size > 0) {
                        val item = data[0]
                        println("Item : ${item.latitude} , ${item.longitude} , ${item.name}")
                        searchWeather(lat = item.latitude, lon = item.longitude, name = item.name)
                    } else {
                        viewModelScope.launch {
                            val msg = "No Item's found"
                            resultChannel.send(ResultEvent.Error(msg))
                            currentError.value = msg
                        }
                    }


                }

                is ApiResponse.Failure.Error -> {
                    viewModelScope.launch {
                        val msg = "No Item's found\nError " + response.statusCode.code
                        resultChannel.send(ResultEvent.Error(msg))
                        currentError.value = msg
                    }
                }

                is ApiResponse.Failure.Exception -> {
                    when (response.exception) {
                        is UnknownHostException -> {
                            viewModelScope.launch {
                                val msg = "No Item's found\nNo Internet Connection!"
                                resultChannel.send(ResultEvent.Error(msg))
                                currentError.value = msg
                            }
                        }
                        else -> {
                            viewModelScope.launch {
                                val msg =
                                    "No Item's found\nException: ${response.exception.message}"
                                resultChannel.send(ResultEvent.Error(msg))
                                currentError.value = msg
                            }
                            throw response.exception
                        }
                    }

                }
            }
        }

    }


    private fun searchWeather(lat: Double, lon: Double, name: String) = viewModelScope.launch {

        val request =
            openWeatherRepository.getWeatherResponse(lon = lon, lat = lat).request { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        currentWeather.value = response.data
                        currentName.value = name

                        viewModelScope.launch {
                            resultChannel.send(ResultEvent.Success)
                        }
                    }

                    is ApiResponse.Failure.Error -> {
                        viewModelScope.launch {
                            resultChannel.send(ResultEvent.Error("No Item's found\nError " + response.statusCode.code))
                        }
                    }

                    is ApiResponse.Failure.Exception -> {
                        when (response.exception) {
                            is UnknownHostException -> {
                                viewModelScope.launch {
                                    resultChannel.send(ResultEvent.Error("No Item's found\nNo Internet Connection!"))
                                }
                            }
                            else -> throw response.exception
                        }
                    }
                }
            }
    }


    companion object {
        private const val CURRENT_QUERY = "current_query"
        const val DEFAULT_QUERY = "Rzeszow"
    }

    sealed class ResultEvent {
        object Success : ResultEvent()
        data class Error(var message: String) : ResultEvent()
    }
}