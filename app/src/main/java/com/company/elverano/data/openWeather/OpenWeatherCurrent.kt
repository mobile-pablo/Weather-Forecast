package com.company.elverano.data.openWeather

import androidx.room.Entity

@Entity(tableName = "open_weather_current")

data class OpenWeatherCurrent(
    val dt: Long = 0,
    val lon: Long =0,
    val lat: Long =0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
    val temp: Double = 0.0,
    val pressure: Int = 0,
    val humidity: Int = 0,
    val clouds: Int = 0,
    val visibility: Int = 0,
    val wind_speed: Double = 0.0,
    val wind_deg: Int = 0,
    val wind_gust: Double = 0.0,
    val weather: List<OpenWeatherResult> = arrayListOf()
) {

    fun getNight(): Boolean? {
        var icon = weather[0].icon
        if (icon.contains("n", true)) {
            return true
        } else if (icon.contains("d", true)) {
            return false
        }

        return null
    }


    data class OpenWeatherResult(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
    )
}
