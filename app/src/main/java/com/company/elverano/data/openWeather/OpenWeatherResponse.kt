package com.company.elverano.data.openWeather

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "open_weather_response")
data class OpenWeatherResponse(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lon") val lon: Double? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("current") val current: OpenWeatherCurrent? = null,
    @SerializedName("hourly") val hourly: List<OpenWeatherHourly>? = null,
    @SerializedName("timezone_offset") val timezone_offset: Int? = null
)