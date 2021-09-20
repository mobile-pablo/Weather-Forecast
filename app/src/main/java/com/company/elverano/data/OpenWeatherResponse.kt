package com.company.elverano.data

data class OpenWeatherResponse (
    val message: String,
    val cod: String,
    val count: Int,
    val list: ArrayList<OpenWeather>
)