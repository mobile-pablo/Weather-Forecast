package com.company.elverano.ui.currentWeather

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
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.databinding.FragmentCurrentBinding
import com.company.elverano.readAsset
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



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            viewModel.resultEvent.collect { event ->
                when (event) {
                    is CurrentWeatherViewModel.ResultEvent.Success -> {
                        Log.d("ResultEvent", "Success")
                    }
                    is CurrentWeatherViewModel.ResultEvent.Error -> {
                        binding.apply {
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

        setHasOptionsMenu(true)
    }

    private fun updateUI(response: OpenWeatherResponse?) {

        binding.apply {
            if (response != null) {
                currentCityBox.visibility = VISIBLE
                currentQueryError.visibility = INVISIBLE
                viewModel.currentError.value = null

                val currentDate = Calendar.getInstance().time
                val sdf = SimpleDateFormat("dd, MMM yyyy")
                currentCityDate.text = sdf.format(currentDate)


                currentCityTemperature.text =
                    "${response.current.temp} C"


                val isNight = response.current.getNight()


                context?.let { context ->
                    isNight?.let { isNight ->
                        val imagePath =
                            setWeatherIcon(response.current.weather[0].id, isNight) + ".png"
                        val x = readAsset(context, imagePath)
                        currentCityImage.setImageBitmap(x)
                    }
                }


                currentCityLat.text = response.lat.toString()
                currentCityLong.text = response.lon.toString()
            } else {
                currentCityBox.visibility = INVISIBLE
                currentQueryError.visibility = VISIBLE
                viewLifecycleOwner.lifecycleScope.launchWhenResumed {
                    viewModel.resultEvent.collect {
                        println("Last result event: $it")
                        if (it is CurrentWeatherViewModel.ResultEvent.Error) {
                            currentQueryError.text = it.message
                        }
                    }
                }
            }
        }
    }

    private fun setWeatherIcon(id: Int, night: Boolean): String {
        resources.apply {
            return when (id) {

                200, 201, 202 -> {
                    if (!night)
                        getString(R.string.clouds_rain_thunder)
                    else
                        getString(R.string.night_clouds_rain_thunder)
                }

                210 -> {
                    if (!night)
                        getString(R.string.cloud_thunder)
                    else
                        getString(R.string.night_clouds_thunder_sky)
                }

                211, 212, 221 -> {
                    if (!night)
                        getString(R.string.clouds_big_thunder)
                    else
                        getString(R.string.night_clouds_big_thunder)
                }

                230, 231, 231 -> {
                    if (!night)
                        getString(R.string.clouds_thunder_drizzle)
                    else
                        getString(R.string.night_clouds_thunder_drizzle)
                }


                300, 301, 701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> {
                    if (!night)
                        getString(R.string.clouds_drizzle)
                    else
                        getString(R.string.night_drizzle)
                }

                302 -> {
                    if (!night)
                        getString(R.string.clouds_big_drizzle)
                    else
                        getString(R.string.night_clouds_big_drizzle)
                }


                310, 311 -> {
                    if (!night)
                        getString(R.string.drizzle_rain)
                    else
                        getString(R.string.night_drizzle_rain)
                }

                312, 313, 314, 321 -> {
                    if (!night)
                        getString(R.string.drizzle_rain)
                    else
                        getString(R.string.night_big_drizzle_rain)
                }


                500, 501, 502, 503, 504, 20, 521, 522, 531 -> {
                    if (!night)
                        getString(R.string.rain)
                    else
                        getString(R.string.night_rain)
                }

                511 -> {
                    if (!night)
                        getString(R.string.freezing_rain)
                    else
                        getString(R.string.night_freezing_rain)
                }

                600, 601 -> {
                    if (!night)
                        getString(R.string.snow)
                    else
                        getString(R.string.night_snow)
                }


                602 -> {
                    if (!night)
                        getString(R.string.big_snow)
                    else
                        getString(R.string.night_big_snow)
                }

                611, 612, 613, 615, 616, 616, 620, 621, 622 -> {
                    if (!night)
                        getString(R.string.rain_snow)
                    else
                        getString(R.string.night_rain_snow)
                }


                800 -> {
                    if (!night)
                        getString(R.string.clear_sky)
                    else
                        getString(R.string.night_clear_sky)
                }


                801, 802, 803, 804 -> {
                    if (!night)
                        getString(R.string.clouds)
                    else
                        getString(R.string.night_clouds)
                }


                else -> {
                    Log.d("OpenWeather", "Wrong ID : $id")
                    getString(R.string.clouds)
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.my_menu, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null) {
                    viewModel.searchLocation(query)
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