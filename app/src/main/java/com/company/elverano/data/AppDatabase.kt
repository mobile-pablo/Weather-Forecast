package com.company.elverano.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.company.elverano.data.error.ErrorDao
import com.company.elverano.data.historyWeather.HistoryWeather
import com.company.elverano.data.historyWeather.HistoryWeatherDao
import com.company.elverano.data.historyWeather.HistoryWeatherResponse
import com.company.elverano.data.openWeather.OpenWeatherDao
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.data.positionStack.PositionStackDao
import com.company.elverano.data.positionStack.PositionStackResponse
import com.company.elverano.di.ApplicationScope
import com.company.elverano.utils.DataConverter
import com.company.elverano.utils.DummyData
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@TypeConverters(DataConverter::class)
@Database(entities = [ PositionStackResponse::class, OpenWeatherResponse::class, HistoryWeatherResponse::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(){

    abstract fun positionStackDao(): PositionStackDao
    abstract fun openWeatherDao(): OpenWeatherDao
    abstract fun historyWeatherDao(): HistoryWeatherDao
    abstract fun errorDao(): ErrorDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            println("Database created")
            applicationScope.launch {
                val list = MutableList(2) { DummyData.dummy_krakow }
                list[0] = DummyData.dummy_krakow
                list[1] = DummyData.dummy_wroclaw
                val response = HistoryWeatherResponse(
                    data = list
                )
             database.get().historyWeatherDao().insertHistoryList(response)
            }
        }
    }
}