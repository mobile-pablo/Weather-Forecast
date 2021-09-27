package com.company.elverano.data.positionStack

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.company.elverano.data.openWeather.OpenWeatherDao
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.utils.DataConverter

@TypeConverters(DataConverter::class)
@Database(entities = [ PositionStackResponse::class], version = 1, exportSchema = false)
abstract class PositionStackDatabase: RoomDatabase(){

    abstract fun positionStackDao(): PositionStackDao
}