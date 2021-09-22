package com.company.elverano.data.positionStack

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PositionStack(
    val latitude : Double,
    val longitude: Double,
    val name: String,
    val region: String,
    val region_code: String,
    val administrative_area: String,
    val country: String,
    val country_code: String,
    val continent: String,
    val label : String
): Parcelable
