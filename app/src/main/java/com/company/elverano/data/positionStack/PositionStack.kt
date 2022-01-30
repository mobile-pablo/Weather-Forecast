package com.company.elverano.data.positionStack

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "position_stack")
data class PositionStack(
    @SerializedName("label") @PrimaryKey(autoGenerate = false) val label: String?=null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("region") val region: String? = null,
    @SerializedName("region_code") val region_code: String? = null,
    @SerializedName("administrative_area") val administrative_area: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("country_code") val country_code: String? = null,
    @SerializedName("continent") val continent: String?=null
)
