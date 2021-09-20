package com.company.elverano.data

import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

@Parcelize
data class OpenWeather(
    val coord: OpenWeatherCoord,
    val sys: OpenWeatherSunData,
    val name: String ="",
    val main: OpenWeatherMain,
    var cod:String="",
    val weather: List<OpenWeatherResult>,
    val timezone: Int
): Parcelable {

 var date = object: Date() {
        val d =  Date()
        val localTime = d.time
        val localOffset = d.timezoneOffset * 60000
        val  utc = localTime + localOffset
        var city = utc + (1000 * timezone)
        val  nd =  Date(city)
    }

// Give it to me in GMT time.

    val isNight =""

    @Parcelize
    data class OpenWeatherCoord(
        val lon: Double =0.0,
        val lat: Double =0.0
    ): Parcelable

    @Parcelize
    data class OpenWeatherMain(
        val temp: Double =0.0,
        val temp_min: Double =0.0,
        val temp_max: Double =0.0,
        val pressure: Double =0.0
    ): Parcelable


    @Parcelize
    data class OpenWeatherSunData(
        val country: String="",
        val sunrise: Long=0,
        val sunset: Long=0
    ): Parcelable

    @Parcelize
    data class OpenWeatherResult(
        val id: Int,
        val main: String,
        val description: String,
        val icon: String
    ): Parcelable
}