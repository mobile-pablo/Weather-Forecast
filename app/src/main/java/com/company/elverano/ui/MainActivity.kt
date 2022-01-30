package com.company.elverano.ui

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.company.elverano.R
import com.company.elverano.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    val mDelegate get() = delegate
    override fun onCreate(savedInstanceState: Bundle?) {

        setUpTheme()
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeActivitySettings()

    }

    private fun initializeActivitySettings() {
        supportActionBar?.hide()
        binding.apply {

            val navController = findNavController(R.id.nav_host_fragment_main)

            bottomNav.setupWithNavController(navController)

        }
    }

    private fun setUpTheme() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = -3
        if (sharedPref.getInt(getString(R.string.theme_mode), defaultValue) == 1) {
            setTheme(R.style.darkTheme)
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        } else {
            setTheme(R.style.Theme_WeatherForecast)
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        }

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar_color)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}