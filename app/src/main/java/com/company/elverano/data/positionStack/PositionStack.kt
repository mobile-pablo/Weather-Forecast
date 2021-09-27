package com.company.elverano.data.positionStack

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "position_stack")

data class PositionStack(
    val latitude : Double=0.0,
    val longitude: Double=0.0,
    val name: String="",
    val region: String="",
    val region_code: String="",
    val administrative_area: String="",
    val country: String="",
    val country_code: String="",
    val continent: String="",
    @PrimaryKey(autoGenerate = false) val label : String=""
)
