package com.company.elverano.ui.currentWeather

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.company.elverano.R
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.databinding.FragmentCurrentBinding
import com.company.elverano.utils.ResultEvent
import com.company.elverano.utils.formatDoubleString
import com.company.elverano.utils.setWeatherIcon
import com.polyak.iconswitch.IconSwitch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class CurrentWeatherFragment : Fragment(R.layout.fragment_current) {

    private val viewModel by viewModels<CurrentWeatherViewModel>()
    private var _binding: FragmentCurrentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCurrentBinding.bind(view)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.themeSwitch.checked = IconSwitch.Checked.RIGHT
        } else {
            binding.themeSwitch.checked = IconSwitch.Checked.LEFT
        }

        addObservers()

        binding.themeSwitch.setCheckedChangeListener { isChecked ->
            if (isChecked  == IconSwitch.Checked.RIGHT) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        collectEvents()
        }
    }

    private fun addObservers() {
        viewModel.currentWeather.observe(viewLifecycleOwner) {
            updateUI(it)
        }
        viewModel.currentName.observe(viewLifecycleOwner) {
            binding.currentCityName.text = it
        }

        viewModel.currentCountry.observe(viewLifecycleOwner) {
            binding.currentCityCountry.text = ", $it"
        }

        viewModel.currentError.observe(viewLifecycleOwner) {
            if (it != null && viewModel.currentWeather.value == null) {
                binding.apply {
                    currentCityBox.visibility = INVISIBLE
                    currentQueryError.visibility = VISIBLE
                    currentQueryError.text = it
                }
            }
        }
    }

    private  suspend fun collectEvents() {
        viewModel.resultEvent.collect { event ->
            when (event) {
                is ResultEvent.Success -> {
                    Log.d("ResultEvent", "Success")
                }
                is ResultEvent.Error -> {
                    binding.apply {
                        binding.currentProgressBar.visibility = INVISIBLE
                        currentCityBox.visibility = INVISIBLE
                        currentQueryError.visibility = VISIBLE
                        currentQueryError.text = event.message
                        viewModel.currentWeather.value = null
                        viewModel.currentName.value = null
                    }
                    Log.d("ResultEvent", "Error")
                }
            }
        }
    }

    private fun updateUI(response: OpenWeatherResponse?) {

        binding.apply {
            if (response != null) {
                binding.currentProgressBar.visibility = INVISIBLE
                currentCityBox.visibility = VISIBLE
                currentQueryError.visibility = INVISIBLE
                viewModel.currentError.value = null

                val currentDate = Date(response.current.dt * 1000)
                val sdf = SimpleDateFormat("dd, MMM yyyy HH:mm:ss")
                currentCityDate.text = sdf.format(currentDate)
                currentCityForecastRecyclerView.visibility = VISIBLE
                currentCityForecastRecyclerView.adapter = CurrentWeatherAdapter(
                    response.hourly,
                    response.timezone_offset,
                    resources
                )
                currentCityForecastRecyclerView.setHasFixedSize(true)
                currentCityForecastRecyclerView.layoutManager = LinearLayoutManager(context).apply {
                    isAutoMeasureEnabled = false
                    orientation = LinearLayoutManager.HORIZONTAL
                }

                currentCityTemperature.text =  formatDoubleString(response.current.temp,1)

                val isNight = response.current.getNight()



                isNight?.let { isNight ->
                    val drawable =
                        setWeatherIcon(
                            response.current.weather[0].id,
                            isNight,
                            resources
                        )

                    context?.let {
                        Glide.with(it)
                            .load(drawable)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(currentCityImage)
                    }
                }



                currentCityLat.text = response.lat.toString()
                currentCityLong.text = response.lon.toString()
            } else {
                currentCityBox.visibility = INVISIBLE
                currentQueryError.visibility = VISIBLE
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    viewModel.resultEvent.collect {
                        if (it is ResultEvent.Error) {
                            currentQueryError.text = it.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}