package com.company.elverano.data.historyWeather

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "history_weather_response")
data class HistoryWeatherResponse(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerializedName("data") val data: MutableList<HistoryWeather>? = null
)
