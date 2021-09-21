package com.company.elverano.data.openWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class OpenWeatherCurrent(
    val dt: Long = 0,
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
) : Parcelable {

    fun getNight(): Boolean? {
        var icon = weather[0].icon
        if (weather[0].icon.contains("n", true)) {
            return true
        } else if (weather[0].icon.contains("d", true)) {
            return false
        }

        return null
    }

    @Parcelize
    data class OpenWeatherResult(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
    ) : Parcelable
}
