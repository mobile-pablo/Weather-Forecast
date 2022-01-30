package com.company.elverano.data.error

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "weather_error")
data class CustomError(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @SerializedName("message") val message: String?=null
)
