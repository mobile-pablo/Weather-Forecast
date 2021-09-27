package com.company.elverano.data.openWeather

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.company.elverano.data.openWeather.OpenWeatherDao
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.data.positionStack.PositionStack
import com.company.elverano.data.positionStack.PositionStackDao
import com.company.elverano.data.positionStack.PositionStackResponse
import com.company.elverano.utils.DataConverter

@TypeConverters(DataConverter::class)
@Database(entities = [OpenWeatherResponse::class], version = 1, exportSchema = false)
abstract class OpenWeatherDatabase: RoomDatabase(){

    abstract fun openWeatherDao(): OpenWeatherDao
}