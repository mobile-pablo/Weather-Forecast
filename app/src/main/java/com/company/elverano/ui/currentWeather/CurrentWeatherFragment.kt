package com.company.elverano.ui.currentWeather

import android.content.Context
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
import com.company.elverano.data.error.CustomError
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.databinding.FragmentCurrentBinding
import com.company.elverano.ui.MainActivity
import com.company.elverano.utils.ResultEvent
import com.company.elverano.utils.fadeIn
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

        addObservers()

        initializeSwitch()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            collectEvents()
        }

        binding.currentProgressBar.visibility = VISIBLE
    }

    private fun initializeSwitch() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val mainActivity = activity as MainActivity
        if (mainActivity.mDelegate.localNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.themeSwitch.checked = IconSwitch.Checked.RIGHT
        } else {
            binding.themeSwitch.checked = IconSwitch.Checked.LEFT
        }

        binding.themeSwitch.setCheckedChangeListener { isChecked ->
            var newTheme = -3
            if (isChecked == IconSwitch.Checked.RIGHT) {
                newTheme = 1
                mainActivity.mDelegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
            } else {
                newTheme = 0
                mainActivity.mDelegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
            }

            with(sharedPref.edit()) {
                putInt(getString(com.company.elverano.R.string.theme_mode), newTheme)
                apply()
            }
        }
    }

    private fun addObservers() {
        viewModel.currentWeather.observe(viewLifecycleOwner) {
            updateUI(it)
        }
        viewModel.currentName.observe(viewLifecycleOwner) {
            binding.apply {
                if (it != null) {
                    currentCityName.text = it
                } else {
                    binding.currentCityName.text = ""
                }
                currentCityName.fadeIn()
            }
        }

        viewModel.currentCountry.observe(viewLifecycleOwner) {
            binding.apply {
                if (it != null) {
                    currentCityCountry.text = ", $it"
                } else {
                    currentCityCountry.text = ""
                }
                currentCityCountry.fadeIn()
            }
        }

        viewModel.currentError.observe(viewLifecycleOwner) {
            if (it != null && viewModel.currentWeather.value == null) {
                binding.apply {
                    currentCityName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    currentCityBox.visibility = INVISIBLE
                    currentQueryError.visibility = VISIBLE
                    currentQueryError.text = it
                }

            }
        }

        viewModel.customError.observe(viewLifecycleOwner) {
            it?.let {
                binding.apply {
                    currentCityBox.visibility = INVISIBLE
                    currentQueryError.visibility = VISIBLE
                    currentQueryError.text = it.message
                }
            }
        }
    }

    private suspend fun collectEvents() {

        viewModel.resultEvent.collect { event ->
            when (event) {
                is ResultEvent.Success -> {
                    Log.d("ResultEvent", "Success")
                }
                is ResultEvent.Error -> {
                    viewModel.insertError(CustomError(message = event.message))
                    binding.apply {
                        binding.currentProgressBar.visibility = INVISIBLE
                        currentCityBox.visibility = INVISIBLE
                        currentQueryError.visibility = VISIBLE
                        currentQueryError.text = event.message
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

                themeSwitch.fadeIn()

                //For unknown reasons GMT time is having 2 hour delay for every country.
                val delay = 2 * 3600 * 1000
                val currentDate =
                    Date(response.current.dt * 1000 + response.timezone_offset * 1000 - delay)
                val sdf = SimpleDateFormat("dd, MMM yyyy HH:mm:ss")
                currentCityDate.text = sdf.format(currentDate)
                currentCityDate.fadeIn()
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

                currentCityTemperature.text = formatDoubleString(response.current.temp, 1)
                currentCityTemperature.fadeIn()
                currentCityMeasure.fadeIn()
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
                currentCityLat.fadeIn()
                currentCityLong.text = response.lon.toString()
                currentCityLong.fadeIn()

                currentCityInfoBox.visibility = VISIBLE
                currentCityName.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_location,
                    0,
                    0,
                    0
                )
                currentCityMeasure.visibility = VISIBLE
            } else {
                currentCityName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {

                    viewModel.resultEvent.collect {
                        if (it is ResultEvent.Error) {
                            currentQueryError.text = it.message
                        }
                    }

                }
            }

            binding.currentProgressBar.visibility = INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}