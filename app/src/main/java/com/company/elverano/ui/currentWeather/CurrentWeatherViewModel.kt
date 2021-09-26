package com.company.elverano.ui.currentWeather


import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
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
    var currentCountry = MutableLiveData<String>()
    var currentError = MutableLiveData<String>()
    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()

    init {
        searchLocation(currentQuery.value.toString())
    }

    fun searchLocation(query: String) = viewModelScope.launch {

        currentQuery.value = query
        positionStackRepository.getLocation(query).request { response ->
            when (response) {
                is ApiResponse.Success -> {
                    val data = response.data.data

                    if (data.size > 0) {
                        val item = data[0]
                        println("Item : ${item.latitude} , ${item.longitude} , ${item.name}")
                        searchWeather(
                            lat = item.latitude,
                            lon = item.longitude,
                            name = item.name,
                            country = item.country
                        )
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


    private fun searchWeather(lat: Double, lon: Double, name: String, country: String) =
        viewModelScope.launch {
                openWeatherRepository.insertWeatherResponse(lon = lon, lat = lat)
           val response =      openWeatherRepository.getWeatherResponse()
            response.collect {
                currentWeather.value = it
                currentName.value = name
                currentCountry.value = country
            }
            viewModelScope.launch {
                resultChannel.send(ResultEvent.Success)
            }
           /*
                openWeatherRepository.getWeatherResponse(lon = lon, lat = lat).request { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            currentWeather.value = response.data
                            currentName.value = name
                            currentCountry.value = country
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
            */
        }


    companion object {
        private const val CURRENT_QUERY = "current_query"
        const val DEFAULT_QUERY = "Rzeszow"
    }

    fun setWeatherIcon(id: Int, night: Boolean, resources: Resources): String {
        resources.apply {
            return when (id) {

                200, 201, 202 -> {
                    if (!night)
                        getString(R.string.clouds_rain_thunder)
                    else
                        getString(R.string.night_clouds_rain_thunder)
                }

                210 -> {
                    if (!night)
                        getString(R.string.cloud_thunder)
                    else
                        getString(R.string.night_clouds_thunder_sky)
                }

                211, 212, 221 -> {
                    if (!night)
                        getString(R.string.clouds_big_thunder)
                    else
                        getString(R.string.night_clouds_big_thunder)
                }

                230, 231, 231 -> {
                    if (!night)
                        getString(R.string.clouds_thunder_drizzle)
                    else
                        getString(R.string.night_clouds_thunder_drizzle)
                }


                300, 301, 701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> {
                    if (!night)
                        getString(R.string.clouds_drizzle)
                    else
                        getString(R.string.night_drizzle)
                }

                302 -> {
                    if (!night)
                        getString(R.string.clouds_big_drizzle)
                    else
                        getString(R.string.night_clouds_big_drizzle)
                }


                310, 311 -> {
                    if (!night)
                        getString(R.string.drizzle_rain)
                    else
                        getString(R.string.night_drizzle_rain)
                }

                312, 313, 314, 321 -> {
                    if (!night)
                        getString(R.string.drizzle_rain)
                    else
                        getString(R.string.night_big_drizzle_rain)
                }


                500, 501, 502, 503, 504, 20, 521, 522, 531 -> {
                    if (!night)
                        getString(R.string.rain)
                    else
                        getString(R.string.night_rain)
                }

                511 -> {
                    if (!night)
                        getString(R.string.freezing_rain)
                    else
                        getString(R.string.night_freezing_rain)
                }

                600, 601 -> {
                    if (!night)
                        getString(R.string.snow)
                    else
                        getString(R.string.night_snow)
                }


                602 -> {
                    if (!night)
                        getString(R.string.big_snow)
                    else
                        getString(R.string.night_big_snow)
                }

                611, 612, 613, 615, 616, 616, 620, 621, 622 -> {
                    if (!night)
                        getString(R.string.rain_snow)
                    else
                        getString(R.string.night_rain_snow)
                }


                800 -> {
                    if (!night)
                        getString(R.string.clear_sky)
                    else
                        getString(R.string.night_clear_sky)
                }


                801, 802, 803, 804 -> {
                    if (!night)
                        getString(R.string.clouds)
                    else
                        getString(R.string.night_clouds)
                }


                else -> {
                    Log.d("OpenWeather", "Wrong ID : $id")
                    getString(R.string.clouds)
                }
            }
        }
    }
}