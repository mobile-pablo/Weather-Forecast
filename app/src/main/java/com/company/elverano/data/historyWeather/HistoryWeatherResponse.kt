package com.company.elverano.data.historyWeather

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "history_weather_response")
data class HistoryWeatherResponse(
    @PrimaryKey(autoGenerate = true) val id : Int =0,
    val data: MutableList<HistoryWeather>? = arrayListOf()
)
