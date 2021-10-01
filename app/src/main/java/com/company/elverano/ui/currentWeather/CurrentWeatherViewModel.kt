package com.company.elverano.ui.currentWeather


import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.elverano.R
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


            val positionStackResponse = positionStackRepository.getLocationFromDatabase()
            positionStackResponse?.let { response ->
                response.data?.let {
                    if (it.size > 0) {
                        currentName.value = it[0].name
                        currentCountry.value = it[0].country
                    }
                }

            }
        }

    }

    private fun searchLocation(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
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
            response.request { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        viewModelScope.launch {
                            openWeatherRepository.deleteWeatherFromDatabase()
                            openWeatherRepository.insertWeatherToDatabase(response.data)

                            response?.data?.let {
                                currentWeather.value = it
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


    fun setWeatherIcon(id: Int, night: Boolean, resources: Resources): Drawable {
        resources.apply {
            return when (id) {
                200, 201, 202 -> {
                    if (!night)
                        getDrawable(R.drawable.clouds_rain_thunder)
                    else
                        getDrawable(R.drawable.night_clouds_rain_thunder)
                }

                210 -> {
                    if (!night)
                        getDrawable(R.drawable.cloud_thunder)
                    else
                        getDrawable(R.drawable.night_clouds_thunder_sky)
                }

                211, 212, 221 -> {
                    if (!night)
                        getDrawable(R.drawable.clouds_big_thunder)
                    else
                        getDrawable(R.drawable.night_clouds_big_thunder)
                }

                230, 231, 231 -> {
                    if (!night)
                        getDrawable(R.drawable.clouds_thunder_drizzle)
                    else
                        getDrawable(R.drawable.night_clouds_thunder_drizzle)
                }


                300, 301, 701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> {
                    if (!night)
                        getDrawable(R.drawable.clouds_drizzle)
                    else
                        getDrawable(R.drawable.night_drizzle)
                }

                302 -> {
                    if (!night)
                        getDrawable(R.drawable.clouds_big_drizzle)
                    else
                        getDrawable(R.drawable.night_clouds_big_drizzle)
                }


                310, 311 -> {
                    if (!night)
                        getDrawable(R.drawable.drizzle_rain)
                    else
                        getDrawable(R.drawable.night_drizzle_rain)
                }

                312, 313, 314, 321 -> {
                    if (!night)
                        getDrawable(R.drawable.drizzle_rain)
                    else
                        getDrawable(R.drawable.night_big_drizzle_rain)
                }


                500, 501, 502, 503, 504, 20, 521, 522, 531 -> {
                    if (!night)
                        getDrawable(R.drawable.rain)
                    else
                        getDrawable(R.drawable.night_rain)
                }

                511 -> {
                    if (!night)
                        getDrawable(R.drawable.freezing_rain)
                    else
                        getDrawable(R.drawable.night_freezing_rain)
                }

                600, 601 -> {
                    if (!night)
                        getDrawable(R.drawable.snow)
                    else
                        getDrawable(R.drawable.night_snow)
                }


                602 -> {
                    if (!night)
                        getDrawable(R.drawable.big_snow)
                    else
                        getDrawable(R.drawable.night_big_snow)
                }

                611, 612, 613, 615, 616, 616, 620, 621, 622 -> {
                    if (!night)
                        getDrawable(R.drawable.rain_snow)
                    else
                        getDrawable(R.drawable.night_rain_snow)
                }


                800 -> {
                    if (!night)
                        getDrawable(R.drawable.clear_sky)
                    else
                        getDrawable(R.drawable.night_clear_sky)
                }


                801, 802, 803, 804 -> {
                    if (!night)
                        getDrawable(R.drawable.clouds)
                    else
                        getDrawable(R.drawable.night_clouds)
                }


                else -> {
                    Log.d("OpenWeather", "Wrong ID : $id")
                    getDrawable(R.drawable.clouds)
                }
            }

        }
    }
}