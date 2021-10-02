package com.company.elverano.data.historyWeather

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.company.elverano.data.openWeather.OpenWeatherCurrent
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.utils.DummyData

@Entity(tableName = "history_weather")
data class HistoryWeather(
    @PrimaryKey(autoGenerate = true)   val db_id: Int =0,
    val temp: Double =0.0,
    val lat: Double =0.0,
    val lon: Double=0.0,
    val name: String ="",
    val weather_id: Int=0,
    val main: String ="",
    val description: String = "",
    val icon: String =""

){
    fun getNight(): Boolean? {
        if (icon.contains("n", true)) {
            return true
        } else if (icon.contains("d", true)) {
            return false
        }

        return null
    }
}
