package com.company.elverano.data.openWeather.query

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.jetbrains.annotations.NotNull

@Dao
interface OpenWeatherQueryDao{

    @NotNull
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(openWeatherQuery: OpenWeatherQuery)


    @NotNull
    @Query("SELECT * FROM open_weather_query")
    fun getQuery() : Flow<OpenWeatherQuery>
}