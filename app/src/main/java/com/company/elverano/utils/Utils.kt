package com.company.elverano.utils

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import com.company.elverano.R

fun setWeatherIcon(id: Int, night: Boolean, resources: Resources): Drawable {
    resources.apply {
        return when (id) {
            200, 201, 202 -> {
                if (!night)
                    getDrawable(R.drawable.clouds_rain_thunder)
                else
                    getDrawable(R.drawable.night_clouds_rain_thunder)
            }

            210 -> {
                if (!night)
                    getDrawable(R.drawable.cloud_thunder)
                else
                    getDrawable(R.drawable.night_clouds_thunder_sky)
            }

            211, 212, 221 -> {
                if (!night)
                    getDrawable(R.drawable.clouds_big_thunder)
                else
                    getDrawable(R.drawable.night_clouds_big_thunder)
            }

            230, 231, 231 -> {
                if (!night)
                    getDrawable(R.drawable.clouds_thunder_drizzle)
                else
                    getDrawable(R.drawable.night_clouds_thunder_drizzle)
            }


            300, 301, 701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> {
                if (!night)
                    getDrawable(R.drawable.clouds_drizzle)
                else
                    getDrawable(R.drawable.night_drizzle)
            }

            302 -> {
                if (!night)
                    getDrawable(R.drawable.clouds_big_drizzle)
                else
                    getDrawable(R.drawable.night_clouds_big_drizzle)
            }


            310, 311 -> {
                if (!night)
                    getDrawable(R.drawable.drizzle_rain)
                else
                    getDrawable(R.drawable.night_drizzle_rain)
            }

            312, 313, 314, 321 -> {
                if (!night)
                    getDrawable(R.drawable.drizzle_rain)
                else
                    getDrawable(R.drawable.night_big_drizzle_rain)
            }


            500, 501, 502, 503, 504, 20, 521, 522, 531 -> {
                if (!night)
                    getDrawable(R.drawable.rain)
                else
                    getDrawable(R.drawable.night_rain)
            }

            511 -> {
                if (!night)
                    getDrawable(R.drawable.freezing_rain)
                else
                    getDrawable(R.drawable.night_freezing_rain)
            }

            600, 601 -> {
                if (!night)
                    getDrawable(R.drawable.snow)
                else
                    getDrawable(R.drawable.night_snow)
            }


            602 -> {
                if (!night)
                    getDrawable(R.drawable.big_snow)
                else
                    getDrawable(R.drawable.night_big_snow)
            }

            611, 612, 613, 615, 616, 616, 620, 621, 622 -> {
                if (!night)
                    getDrawable(R.drawable.rain_snow)
                else
                    getDrawable(R.drawable.night_rain_snow)
            }


            800 -> {
                if (!night)
                    getDrawable(R.drawable.clear_sky)
                else
                    getDrawable(R.drawable.night_clear_sky)
            }


            801, 802, 803, 804 -> {
                if (!night)
                    getDrawable(R.drawable.clouds)
                else
                    getDrawable(R.drawable.night_clouds)
            }


            else -> {
                Log.d("OpenWeather", "Wrong ID : $id")
                getDrawable(R.drawable.clouds)
            }
        }

    }
}


fun formatDoubleString(x: Double, places: Int): String {
    return String.format("%." + places + "f", x).replace(",", ".")
}


inline fun View.fadeIn(durationMillis: Long = 1000) {
    this.startAnimation(AlphaAnimation(0F, 1F).apply {
        duration = durationMillis
        fillAfter = true
    })
}

inline fun View.fadeOut(durationMillis: Long = 1000) {
    this.startAnimation(AlphaAnimation(1F, 0F).apply {
        duration = durationMillis
        fillAfter = true
    })
}