package com.company.elverano.ui.currentWeather


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.elverano.data.error.CustomError
import com.company.elverano.data.error.ErrorRepository
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
    private val positionStackRepository: PositionStackRepository,
    private val errorRepository: ErrorRepository
) : ViewModel() {

    var currentWeather = MutableLiveData<OpenWeatherResponse>()
    var currentName = MutableLiveData<String>()
    var currentCountry = MutableLiveData<String>()
    var currentError = MutableLiveData<String>()

    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()

    private var searchJob: Job? = null
    private var couritineJob: Job? = null
    private var messageJob: Job? = null

    private var _customError = MutableLiveData<CustomError>()
    val customError: LiveData<CustomError> get() = _customError

    init {
        initializeVm()
    }

    private fun initializeVm() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val openWeatherResponse = openWeatherRepository.getWeatherFromDB()
            openWeatherResponse?.let {
                currentWeather.value = it
            } ?: kotlin.run {
                searchLocation("Warsaw")
            }

            positionStackRepository.getLocationFromDatabase()?.data?.let {
                if (it.isNotEmpty()) {
                    currentName.value = it[0].name!!
                    currentCountry.value = it[0].country_code!!
                }
            }

            errorRepository.getErrorFromDB()?.let {
                _customError.value = it
            }
        }
    }

    private fun searchLocation(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            positionStackRepository.getLocationFromAPI(query)?.request { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        messageJob?.cancel()
                        messageJob = viewModelScope.launch {
                            positionStackRepository.deletePositionFromDB()
                            positionStackRepository.insertPositionToDB(response.data)
                            response.data.data.let { data ->
                                if (data!!.isNotEmpty()) {
                                    data[0].let { item ->
                                        searchWeather(
                                            lat = item.latitude!!,
                                            lon = item.longitude!!,
                                            name = item.name!!,
                                            country = item.country!!
                                        )

                                        Log.d(
                                            "CurrentWeather",
                                            "Item : ${item.latitude} , ${item.longitude} , ${item.name}"
                                        )
                                    }
                                } else {
                                    val msg = "No Item's found"
                                    resultChannel.send(ResultEvent.Error(msg))
                                    currentError.value = msg
                                }
                            }
                        }
                    }
                    is ApiResponse.Failure.Error -> {
                        messageJob?.cancel()
                        messageJob = viewModelScope.launch {
                            val msg = "No Item's found\nError " + response.statusCode.code
                            resultChannel.send(ResultEvent.Error(msg))
                        }
                    }
                    is ApiResponse.Failure.Exception -> {
                        messageJob?.cancel()
                        messageJob = viewModelScope.launch {
                            when (response.exception) {
                                is UnknownHostException -> {
                                    val msg = "No Item's found\nNo Internet Connection!"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                                else -> {
                                    val msg =
                                        "No Item's found\nException: ${response.exception.message}"
                                    resultChannel.send(ResultEvent.Error(msg))
                                    throw response.exception
                                }
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
            openWeatherRepository.getWeatherFromAPI(lon = lon, lat = lat)?.request { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        messageJob?.cancel()
                        messageJob = viewModelScope.launch {
                            response.data.let {
                                openWeatherRepository.deleteWeatherFromDatabase()
                                openWeatherRepository.insertWeatherToDatabase(it)

                                currentWeather.value = it
                                currentName.value = name
                                currentCountry.value = country
                                resultChannel.send(ResultEvent.Success)
                            }
                        }
                    }

                    is ApiResponse.Failure.Error -> {
                        messageJob?.cancel()
                        messageJob = viewModelScope.launch {
                            val msg = "No Item's found\nError " + response.statusCode.code
                            resultChannel.send(ResultEvent.Error(msg))
                        }
                    }

                    is ApiResponse.Failure.Exception -> {
                        messageJob?.cancel()
                        messageJob = viewModelScope.launch {
                            when (response.exception) {
                                is UnknownHostException -> {
                                    val msg = "No Item's found\nNo Internet Connection!"
                                    resultChannel.send(ResultEvent.Error(msg))
                                }
                                else -> {
                                    val msg =
                                        "No Item's found\nException: ${response.exception.message}"
                                    resultChannel.send(ResultEvent.Error(msg))
                                    throw response.exception
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun insertError(customError: CustomError) {
        messageJob?.cancel()
        messageJob = viewModelScope.launch {
            errorRepository.deleteErrorFromDatabase()
            errorRepository.insertErrorToDatabase(customError)
        }
    }

    fun deleteError() {
        messageJob?.cancel()
        messageJob = viewModelScope.launch {
            errorRepository.deleteErrorFromDatabase()
        }
    }
}