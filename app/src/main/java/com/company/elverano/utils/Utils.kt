package com.company.elverano.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity.apply
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.res.ResourcesCompat
import com.company.elverano.R

fun setWeatherIcon(id: Int, night: Boolean, resources: Resources): Drawable? {
        return when (id) {
            200, 201, 202 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.clouds_rain_thunder,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_clouds_rain_thunder,null)
            }

            210 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.cloud_thunder,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_clouds_thunder_sky,null)
            }

            211, 212, 221 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.clouds_big_thunder,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_clouds_big_thunder,null)
            }

            230, 231, 231 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.clouds_thunder_drizzle,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_clouds_thunder_drizzle,null)
            }


            300, 301, 701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.clouds_drizzle,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_drizzle,null)
            }

            302 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.clouds_big_drizzle,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_clouds_big_drizzle,null)
            }


            310, 311 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.drizzle_rain,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_drizzle_rain,null)
            }

            312, 313, 314, 321 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.drizzle_rain,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_big_drizzle_rain,null)
            }


            500, 501, 502, 503, 504, 20, 521, 522, 531 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.rain,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_rain,null)
            }

            511 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.freezing_rain,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_freezing_rain,null)
            }

            600, 601 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.snow,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_snow,null)
            }


            602 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.big_snow,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_big_snow,null)
            }

            611, 612, 613, 615, 616, 616, 620, 621, 622 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.rain_snow,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_rain_snow,null)
            }


            800 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.clear_sky,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_clear_sky,null)
            }


            801, 802, 803, 804 -> {
                if (!night)
                    ResourcesCompat.getDrawable(resources,R.drawable.clouds,null)
                else
                    ResourcesCompat.getDrawable(resources,R.drawable.night_clouds,null)
            }


            else -> {
                Log.d("OpenWeather", "Wrong ID : $id")
                ResourcesCompat.getDrawable(resources,R.drawable.clouds,null)
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