package com.company.elverano

import com.company.elverano.data.openWeather.OpenWeatherCurrent
import com.company.elverano.data.openWeather.OpenWeatherResponse

object dummy {
    val dummy_current_wroclaw = OpenWeatherCurrent( temp =  16.48,weather = arrayListOf(
        OpenWeatherCurrent.OpenWeatherResult(id=801, main="Clouds",description = "few clouds", icon = "02d")))

    val dummy_current_krakow = OpenWeatherCurrent( temp =  12.74,weather = arrayListOf(
        OpenWeatherCurrent.OpenWeatherResult(id=500, main="Rain",description = "light rain", icon = "10d")))
    val dummy_wroclaw = OpenWeatherResponse(
        lat =51.0973,
        lon = 17.024,
        name = "Wrocław",
        current = dummy_current_wroclaw,

    )

    val dummy_krakow = OpenWeatherResponse(
        lat =50.0527,
        lon = 19.9873,
        name = "Krakow",
        current = dummy_current_krakow
    )




}