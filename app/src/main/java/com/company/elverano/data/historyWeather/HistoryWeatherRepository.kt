package com.company.elverano.data.historyWeather

import com.company.elverano.data.AppDatabase
import javax.inject.Inject

class HistoryWeatherRepository @Inject constructor(
    database: AppDatabase
) {
    private val dao =
        database.historyWeatherDao()

    suspend fun getLocationFromDatabase() =
        dao.getHistory()

    suspend fun insertResponseToDb(data: HistoryWeatherResponse) =
        dao.insertHistoryList(data)

    suspend fun deleteHistoryList() =
        dao.deleteHistoryList()
}