package com.company.elverano.data.openWeather

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Entity(tableName = "open_weather_response")
@Parcelize
data class OpenWeatherResponse (
     @PrimaryKey(autoGenerate = true)   val id: Int=0,
     val lat: Double=0.0,
     val lon: Double=0.0,
     var name: String="",
     val current: OpenWeatherCurrent= OpenWeatherCurrent(),
     val hourly: ArrayList<OpenWeatherHourly> = arrayListOf(),
     val daily: ArrayList<OpenWeatherDaily> = arrayListOf(),
     val timezone_offset: Int =0
): Parcelable