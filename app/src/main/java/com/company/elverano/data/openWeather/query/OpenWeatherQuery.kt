package com.company.elverano.data.openWeather.query

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "open_weather_query")
data class OpenWeatherQuery(
    @PrimaryKey(autoGenerate = true)  val id: Int=0,
    val query: String ="Warsaw"
)