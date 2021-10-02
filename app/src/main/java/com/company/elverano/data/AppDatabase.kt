package com.company.elverano.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.company.elverano.data.openWeather.OpenWeatherDao
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.data.positionStack.PositionStackDao
import com.company.elverano.data.positionStack.PositionStackResponse
import com.company.elverano.utils.DataConverter

@TypeConverters(DataConverter::class)
@Database(entities = [ PositionStackResponse::class, OpenWeatherResponse::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun positionStackDao(): PositionStackDao
    abstract fun openWeatherDao(): OpenWeatherDao
}