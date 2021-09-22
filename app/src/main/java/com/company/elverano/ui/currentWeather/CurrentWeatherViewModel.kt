package com.company.elverano.ui.currentWeather


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.elverano.data.openWeather.OpenWeatherRepository
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.data.positionStack.PositionStack
import com.company.elverano.data.positionStack.PositionStackRepository
import com.company.elverano.data.positionStack.PositionStackResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CurrentWeatherViewModel @Inject constructor(
    private val openWeatherRepository: OpenWeatherRepository,
    private val positionStackRepository: PositionStackRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    var currentWeather = MutableLiveData<OpenWeatherResponse>()
    var currentName = MutableLiveData<String>()
    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)
    private val resultChannel = Channel<ResultEvent>()
    val resultEvent = resultChannel.receiveAsFlow()

    init {
        searchLocation(DEFAULT_QUERY)
    }

    fun searchLocation(query: String) = viewModelScope.launch {
        currentQuery.value = query
        val positionStackResponse = positionStackRepository.getLocation(query).body()

        val data = positionStackResponse?.data
        if (data != null) {
            if (data.size > 0) {
                val item = data[0]
                println("Item : ${item.latitude} , ${item.longitude} , ${item.name}")
                searchWeather(lat = item.latitude, lon = item.longitude,name = item.name)
            } else {
                resultChannel.send(ResultEvent.Error("No Item's found"))
            }
        } else {
            resultChannel.send(ResultEvent.Error("No Item's found"))
        }

    }


    fun searchWeather(lat: Double, lon: Double, name: String) = viewModelScope.launch {
        val response = openWeatherRepository.getWeatherResponse(lon = lon, lat = lat).body()

        response?.let {
            currentWeather.value = it
            currentName.value =name
            resultChannel.send(ResultEvent.Success)
        } ?: run {
            resultChannel.send(ResultEvent.Error("No Item's found"))
        }
    }


    companion object {
        private const val CURRENT_QUERY = "current_query"
        const val DEFAULT_QUERY = "Warsaw"
    }

    sealed class ResultEvent {
        object Success: ResultEvent()
        data class Error(var message: String) : ResultEvent()
    }
}