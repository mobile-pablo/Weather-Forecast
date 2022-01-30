package com.company.elverano.data.openWeather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import org.jetbrains.annotations.NotNull

@Dao
interface OpenWeatherDao {
    @NotNull
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(openWeatherResponse: OpenWeatherResponse)

    @Query("SELECT * FROM open_weather_response")
    suspend fun getWeather(): OpenWeatherResponse?

    @Query("DELETE FROM open_weather_response")
    suspend fun deleteWeather()
}