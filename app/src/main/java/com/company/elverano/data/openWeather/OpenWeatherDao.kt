package com.company.elverano.data.openWeather

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull

@Dao
interface OpenWeatherDao {


    @NotNull
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(openWeatherResponse: OpenWeatherResponse)

    @Query("SELECT * FROM open_weather_response")
    suspend fun getWeather() : OpenWeatherResponse?


    @Query("DELETE FROM open_weather_response")
    suspend fun deleteWeather()


}