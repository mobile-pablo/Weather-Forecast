package com.company.elverano.data.historyWeather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import org.jetbrains.annotations.NotNull

@Dao
interface HistoryWeatherDao {
    @NotNull
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryList(response: HistoryWeatherResponse)

    @NotNull
    @Query("SELECT * FROM history_weather_response")
    suspend fun getHistory(): HistoryWeatherResponse?

    @Query("DELETE FROM history_weather_response")
    suspend fun deleteHistoryList()
}