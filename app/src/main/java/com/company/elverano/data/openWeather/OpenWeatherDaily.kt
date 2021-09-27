package com.company.elverano.data.openWeather

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize
@Entity(tableName = "open_weather_daily")

data class OpenWeatherDaily(
    val dt: Long = 0,
    val sunrise: Long = 0,
    val sunset: Long = 0,
    val moonrise: Long = 0,
    val moonset: Long = 0,
    val moon_phase: Double = 0.0,
    val temp: OpenWeatherDailyTemp,
    val pressure: Int = 0,
    val humidity: Int = 0,
    val wind_speed: Double = 0.0,
    val wind_deg: Int = 0,
    val wind_gust: Double = 0.0,
    val weather: ArrayList<OpenWeatherCurrent.OpenWeatherResult>,
    val clouds: Int = 0,
    val pop: Double = 0.0,
    val uvi: Double = 0.0
) {

    data class OpenWeatherDailyTemp(
        val day: Double = 0.0,
        val min: Double = 0.0,
        val max: Double = 0.0,
        val night: Double = 0.0,
        val eve: Double = 0.0,
        val morn: Double = 0.0
    )
}