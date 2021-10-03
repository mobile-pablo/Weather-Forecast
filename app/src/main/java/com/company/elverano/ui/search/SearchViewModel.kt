package com.company.elverano.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.elverano.data.historyWeather.HistoryWeather
import com.company.elverano.data.historyWeather.HistoryWeatherRepository
import com.company.elverano.data.historyWeather.HistoryWeatherResponse
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
    private val openWeatherRepository: OpenWeatherRepository,
    private val historyWeatherRepository: HistoryWeatherRepository
) : ViewModel() {
    private var searchJob: Job? = null
    private var couritineJob: Job? = null
    var currentError = MutableLiveData<String>()
    private var  _historyResponse = MutableLiveData<HistoryWeatherResponse>()
    val historyResponse : LiveData<HistoryWeatherResponse>  get() = _historyResponse

    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()

    private var _weatherResponse = MutableLiveData<OpenWeatherResponse>()
    val weatherResponse: LiveData<OpenWeatherResponse> get() = _weatherResponse

    init {
        viewModelScope.launch {
            _weatherResponse.value = openWeatherRepository.getWeatherFromDB()
            _historyResponse.value = historyWeatherRepository.getLocationFromDatabase()
            downloadHistory()
        }
    }

    fun searchLocation(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {

           val oldPositionStack =  positionStackRepository.getLocationFromDatabase()
            oldPositionStack?.let {
                if(!it.data.isNullOrEmpty()){
                    updateHistory(weatherResponse.value, it.data[0].name)
                }
            }


            val response = positionStackRepository.getLocationFromAPI(query)
            response.request { apiResponse ->
                when (apiResponse) {
                    is ApiResponse.Success -> {
                        viewModelScope.launch {

                        apiResponse.data?.let {
                            positionStackRepository.deletePositionFromDB()
                            positionStackRepository.insertPositionToDB(it)

                            val data = it.data

                            data?.let {
                                if (it == null) {
                                    viewModelScope.launch {
                                        val msg = "No Item's found"
                                        resultChannel.send(ResultEvent.Error(msg))
                                        currentError.value = msg
                                    }
                                } else {
                                    if (it.isNotEmpty()) {
                                        val item = it[0]
                                        searchWeather(
                                            lat = item.latitude,
                                            lon = item.longitude,
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


                    }
                    is ApiResponse.Failure.Error -> {
                        viewModelScope.launch {
                            val msg = "No Item's found\nError " + apiResponse.statusCode.code
                            resultChannel.send(ResultEvent.Error(msg))
                        }


                    }
                    is ApiResponse.Failure.Exception -> {
                        when (apiResponse.exception) {
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
                                        "No Item's found\nException: ${apiResponse.exception.message}"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                                throw apiResponse.exception
                            }
                        }


                    }
                }
            }

        }
    }

    private suspend fun updateHistory(value: OpenWeatherResponse?, name: String) {
        value?.let { response ->
            val list = historyResponse.value?.data
            list?.let {
                val backup =it[0]
                    it[1] = backup
                it[0] = HistoryWeather(
                    lat = response.lat,
                    lon = response.lon,
                    name = name,
                    weather_id = response.current.weather[0].id,
                    temp = response.current.temp,
                    main = response.current.weather[0].main,
                    description = response.current.weather[0].description,
                    icon = response.current.weather[0].icon,
                )

                historyResponse.value?.let {
                    historyWeatherRepository.deleteHistoryList()
                    historyWeatherRepository.insertResponseToDb(it)
                }

            }

        }
    }

    private suspend fun downloadHistory() {
        //Old weather is added to list. Two item's of search history  (not current weather)  will be displayed in Fragment
        historyWeatherRepository.getLocationFromDatabase().let {
            val first = it?.data?.get(0)
            first?.let { weather ->
                historyResponse.value?.data?.set(0, weather)
            }

            val second = it?.data?.get(1)
            second?.let { weather ->
                historyResponse.value?.data?.set(1, weather)
            }
        }
    }

    private fun searchWeather(lat: Double, lon: Double) {
        couritineJob?.cancel()
        couritineJob = viewModelScope.launch {
            openWeatherRepository.getWeatherFromAPI(lon = lon, lat = lat).request {
                when (it) {
                    is ApiResponse.Success -> {
                        viewModelScope.launch {
                            val item = it.data
                           item?.let {
                               openWeatherRepository.deleteWeatherFromDatabase()
                               openWeatherRepository.insertWeatherToDatabase(item)
                               resultChannel.send(ResultEvent.Success)
                           } ?: run {
                               val msg = "No Item's found\nError " + it.statusCode.code
                               resultChannel.send(ResultEvent.Error(msg))
                           }
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