package com.company.elverano.utils

import androidx.room.TypeConverter
import com.company.elverano.data.historyWeather.HistoryWeather
import com.company.elverano.data.openWeather.OpenWeatherCurrent
import com.company.elverano.data.openWeather.OpenWeatherHourly
import com.company.elverano.data.positionStack.PositionStack
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {

    @TypeConverter
    fun fromOpenWeatherCurrent(value: OpenWeatherCurrent): String {
        val gson = Gson()
        val type = object : TypeToken<OpenWeatherCurrent>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toOpenWeatherCurrent(value: String): OpenWeatherCurrent {
        val gson = Gson()
        val type = object : TypeToken<OpenWeatherCurrent>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromOpenWeatherHourlyList(value: ArrayList<OpenWeatherHourly>): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<OpenWeatherHourly>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toOpenWeatherHourlyList(value: String): ArrayList<OpenWeatherHourly> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<OpenWeatherHourly>>() {}.type
        return gson.fromJson(value, type)
    }


    @TypeConverter
    fun fromPositionList(value: List<PositionStack>): String {
        val gson = Gson()
        val type = object : TypeToken<List<PositionStack>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toPositionList(value: String): List<PositionStack> {
        val gson = Gson()
        val type = object : TypeToken<List<PositionStack>>() {}.type
        return gson.fromJson(value, type)
    }


    @TypeConverter
    fun fromHistoryListList(value: List<HistoryWeather>): String {
        val gson = Gson()
        val type = object : TypeToken<List<HistoryWeather>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toHistoryListList(value: String): List<HistoryWeather> {
        val gson = Gson()
        val type = object : TypeToken<List<HistoryWeather>>() {}.type
        return gson.fromJson(value, type)
    }

}