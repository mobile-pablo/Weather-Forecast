package com.company.elverano.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.elverano.data.openWeather.OpenWeatherRepository
import com.company.elverano.data.openWeather.OpenWeatherResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val openWeatherRepository: OpenWeatherRepository
) : ViewModel() {
    private var _weatherResponse = MutableLiveData<OpenWeatherResponse>()
    val weatherResponse: LiveData<OpenWeatherResponse> get() = _weatherResponse

    private var downloadJob: Job? = null

    init {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            _weatherResponse.value = openWeatherRepository.getWeatherFromDB()
        }
    }
}