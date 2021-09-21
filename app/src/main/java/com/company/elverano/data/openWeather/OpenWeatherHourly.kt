package com.company.elverano.data.openWeather

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OpenWeatherHourly(
    val pop: Int
) : OpenWeatherCurrent(), Parcelable
