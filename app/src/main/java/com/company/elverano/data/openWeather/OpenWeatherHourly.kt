package com.company.elverano.data.openWeather

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "open_weather_hourly")
data class OpenWeatherHourly(
    @SerializedName("dt") val dt: Long? = null,
    @SerializedName("lon") val lon: Long? = null,
    @SerializedName("lat") val lat: Long? = null,
    @SerializedName("sunrise") val sunrise: Long? = null,
    @SerializedName("sunset") val sunset: Long? = null,
    @SerializedName("temp") val temp: Double? = null,
    @SerializedName("pressure") val pressure: Int? = null,
    @SerializedName("humidity") val humidity: Int? = null,
    @SerializedName("clouds") val clouds: Int? = null,
    @SerializedName("visibility") val visibility: Int? = null,
    @SerializedName("wind_speed") val wind_speed: Double? = null,
    @SerializedName("wind_deg") val wind_deg: Int? = null,
    @SerializedName("wind_gust") val wind_gust: Double? = null,
    @SerializedName("weather") val weather: List<OpenWeatherCurrent.OpenWeatherResult>? = null,
    @SerializedName("pop") val pop: Double? = null,
) {
    fun getNight(): Boolean? {
        val icon = weather?.get(0)!!.icon!!
        if (icon.contains("n", true)) {
            return true
        } else if (icon.contains("d", true)) {
            return false
        }
        return null
    }
}
