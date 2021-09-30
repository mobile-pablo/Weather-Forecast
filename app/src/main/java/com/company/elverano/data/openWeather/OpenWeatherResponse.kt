package com.company.elverano.data.openWeather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "open_weather_response")

data class OpenWeatherResponse (
     @PrimaryKey(autoGenerate = true) val id: Int=0,
     val lat: Double=0.0,
     val lon: Double=0.0,
     var name: String="",
     val current: OpenWeatherCurrent= OpenWeatherCurrent(),
     val hourly: ArrayList<OpenWeatherHourly> = arrayListOf(),
     val timezone_offset: Int =0
)