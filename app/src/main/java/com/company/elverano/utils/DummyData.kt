package com.company.elverano.utils

import com.company.elverano.data.historyWeather.HistoryWeather

object DummyData {


    val dummy_wroclaw = HistoryWeather(
        lat = 51.0973,
        lon = 17.024,
        name = "Wrocław",
        temp = 16.48,
        weather_id = 801,
        main = "Clouds",
        description = "few clouds",
        icon = "02d",


        )

    val dummy_krakow = HistoryWeather(
        lat = 50.0527,
        lon = 19.9873,
        name = "Krakow",
        temp = 12.74,
        weather_id = 500,
        main = "Rain",
        description = "light rain",
        icon = "10d"
    )


}