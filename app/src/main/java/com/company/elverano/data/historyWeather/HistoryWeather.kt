package com.company.elverano.data.historyWeather

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "history_weather")
data class HistoryWeather(
    @SerializedName("db_id") @PrimaryKey(autoGenerate = true) val db_id: Int? = null,
    @SerializedName("temp") val temp: Double? = null,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lon") val lon: Double? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("weather_id") val weather_id: Int? = null,
    @SerializedName("main") val main: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("icon") val icon: String? = null
) {
    fun getNight(): Boolean? {
        if (icon!!.contains("n", true)) {
            return true
        } else if (icon.contains("d", true)) {
            return false
        }
        return null
    }
}
