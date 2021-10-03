package com.company.elverano.data.error

import androidx.room.Entity

@Entity(tableName = "weather_error")
data class Error(
    val message :String
)
