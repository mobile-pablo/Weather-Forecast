package com.company.elverano.data.openWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OpenWeatherResponse (
val lat: Double,
val lon: Double,
val name: String,
val current: OpenWeatherCurrent,
val hourly: ArrayList<OpenWeatherHourly>,
val daily: ArrayList<OpenWeatherDaily>
): Parcelable