package com.company.elverano.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.company.elverano.R
import com.company.elverano.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        setUpTheme()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.apply {

            val navController = findNavController(R.id.nav_host_fragment_main)

            bottomNav.setupWithNavController(navController)

        }

    }

    private fun setUpTheme() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = -3
        val savedTheme = sharedPref.getInt(getString(R.string.theme_mode), defaultValue)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        if (savedTheme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.darkTheme)
            window.statusBarColor = Color.parseColor("#141414")
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.Theme_WeatherForecast)
            window.statusBarColor = Color.parseColor("#B04349")
        }

        /**
         *  I know that we called already mode night and day in CurrentWeatherFragment, but unfortunately we need to call it again,
         *  becouse it wont be called when I restart app. Also I need to call same function there only because It wont switch into this place
         */

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}