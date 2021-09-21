package com.company.elverano.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.company.elverano.R
import com.company.elverano.data.OpenWeather
import com.company.elverano.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel by viewModels<MainViewModel>()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMainBinding.bind(view)

        setHasOptionsMenu(true)

        viewModel.currentWeather.observe(viewLifecycleOwner) {
            updateUI(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
           // viewModel.searchWeather(MainViewModel.DEFAULT_QUERY)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.resultEvent.collect { event ->
                when (event) {
                    is MainViewModel.ResultEvent.Success -> { Log.d("ResultEvent", "Success")}
                    is MainViewModel.ResultEvent.Error -> {
                        binding.cityBox.visibility = INVISIBLE
                        binding.textViewQueryError.visibility = VISIBLE
                        binding.textViewQueryError.text = event.message
                    }
                }
            }
        }
    }

    private fun updateUI(openWeather: OpenWeather) {
        binding.apply {

            cityBox.visibility = VISIBLE
            textViewQueryError.visibility = INVISIBLE

            mainCityName.text = openWeather.name
            mainCityTemperature.text =
                "${openWeather.main.temp}${resources.getString(R.string.wi_celsius)}"


            val isNight = openWeather.getNight()
            mainCityFontImg.text =
                isNight?.let {
                    setWeatherIcon(openWeather, it)
                }

            mainCityLat.text = openWeather.coord.lat.toString()
            mainCityLong.text = openWeather.coord.lon.toString()
        }
    }

    private fun setWeatherIcon(openWeather: OpenWeather, isNight: Boolean): String {
        val id = openWeather.weather[0].id
        resources.apply {
            val thunderstormId: IntRange = 200.rangeTo(232)
            val drizzleId: IntRange = 300.rangeTo(321)
            val rainId: IntRange = 500.rangeTo(531)
            val snowId: IntRange = 600.rangeTo(622)
            val atmosphereId: IntRange = 701.rangeTo(781)
            val clearSky = 800
            val cloudsId: IntRange = 801.rangeTo(804)


            if (isBetween(id, thunderstormId)) {
                if (!isNight)
                    return getString(R.string.wi_day_thunderstorm)
                else
                    return getString(R.string.wi_night_thunderstorm)
            } else if (isBetween(id, drizzleId)) {
                if (!isNight)
                    return getString(R.string.wi_day_sleet)
                else
                    return getString(R.string.wi_night_sleet)
            } else if (isBetween(id, rainId)) {
                if (!isNight)
                    return getString(R.string.wi_day_rain)
                else
                    return getString(R.string.wi_night_rain)
            } else if (isBetween(id, snowId)) {
                if (!isNight)
                    return getString(R.string.wi_day_snow)
                else
                    return getString(R.string.wi_night_snow)
            } else if (isBetween(id, atmosphereId)) {
                if (!isNight)
                    return getString(R.string.wi_day_fog)
                else
                    return getString(R.string.wi_night_fog)
            } else if (id == clearSky) {

                if (!isNight)
                    return getString(R.string.wi_day_sunny)
                else
                    return getString(R.string.wi_night_clear)

            } else if (isBetween(id, cloudsId)) {
                if (!isNight)
                    return getString(R.string.wi_day_cloudy)
                else
                    return getString(R.string.wi_night_cloudy)
            } else {
                return "Error"
            }

        }
    }

    fun isBetween(x: Int, range: IntRange): Boolean {
        return range.first <= x && x <= range.last
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.my_menu, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    viewModel.searchWeather(query)
                    searchView.setQuery("", false)
                    searchView.clearFocus()
                    searchItem.collapseActionView()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}