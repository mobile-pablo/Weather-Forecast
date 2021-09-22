package com.company.elverano.data.openWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OpenWeatherHourly(
    val pop: Double=0.0
) : OpenWeatherCurrent(), Parcelable
