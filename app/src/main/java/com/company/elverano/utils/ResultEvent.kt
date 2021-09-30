package com.company.elverano.utils

import com.company.elverano.data.openWeather.OpenWeatherResponse

sealed class ResultEvent {
    object Success : ResultEvent()
    data class Error(var message: String) : ResultEvent()
}