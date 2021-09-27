package com.company.elverano.data.openWeather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "open_weather_response")

data class OpenWeatherResponse (
     val lat: Double=0.0,
     val lon: Double=0.0,
     @PrimaryKey(autoGenerate = false)    var name: String="",
     val current: OpenWeatherCurrent= OpenWeatherCurrent(),
     val hourly: ArrayList<OpenWeatherHourly> = arrayListOf(),
     val daily: ArrayList<OpenWeatherDaily> = arrayListOf(),
     val timezone_offset: Int =0
)