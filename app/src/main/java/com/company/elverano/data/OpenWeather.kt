package com.company.elverano.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

@Parcelize
data class OpenWeather(
    val coord: OpenWeatherCoord,
    val weather: List<OpenWeatherResult>,
    val main: OpenWeatherMain,
    val dt: Int,
    val sys: OpenWeatherSunData,
    val timezone: Int,
    val name: String = "",

): Parcelable {

// Give it to me in GMT time.

    fun getNight(): Boolean? {
        var icon= weather[0].icon
       if(weather[0].icon.contains("n",true)){
           return true
       }else if(weather[0].icon.contains("d",true)){
           return  false
       }

        return null
    }


    @Parcelize
    data class OpenWeatherCoord(
        val lon: Double = 0.0,
        val lat: Double = 0.0
    ): Parcelable

    @Parcelize
    data class OpenWeatherMain(
        val temp: Double = 0.0,
        val temp_min: Double = 0.0,
        val temp_max: Double = 0.0,
        val pressure: Double = 0.0
    ): Parcelable


    @Parcelize
    data class OpenWeatherSunData(
        val country: String = "",
        val sunrise: Long = 0,
        val sunset: Long = 0
    ): Parcelable

    @Parcelize
    data class OpenWeatherResult(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
    ): Parcelable
}