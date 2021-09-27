package com.company.elverano.data.openWeather

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.company.elverano.data.openWeather.query.OpenWeatherQuery
import com.company.elverano.data.openWeather.query.OpenWeatherQueryDao
import com.company.elverano.utils.DataConverter

@TypeConverters(DataConverter::class)
@Database(entities = [OpenWeatherResponse::class, OpenWeatherQuery::class], version = 4, exportSchema = true)
abstract class OpenWeatherDatabase: RoomDatabase(){

    abstract fun openWeatherDao(): OpenWeatherDao
    abstract fun openWeatherQueryDao(): OpenWeatherQueryDao
}