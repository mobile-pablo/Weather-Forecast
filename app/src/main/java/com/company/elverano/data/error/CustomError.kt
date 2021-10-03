package com.company.elverano.data.error

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_error")
data class CustomError(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val message :String
)
