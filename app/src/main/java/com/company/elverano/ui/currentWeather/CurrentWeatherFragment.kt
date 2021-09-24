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
import androidx.recyclerview.widget.LinearLayoutManager
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

    lateinit var adapter: CurrentWeatherAdapter
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

                val currentDate = Date(response.current.dt * 1000)
                val sdf = SimpleDateFormat("dd, MMM yyyy HH:mm:ss")
                currentCityDate.text = sdf.format(currentDate)
                currentCityForecastRecyclerView.adapter = CurrentWeatherAdapter(
                    response.hourly,
                    response.timezone_offset,
                    viewModel,
                    resources
                )
                currentCityForecastRecyclerView.setHasFixedSize(true)
              currentCityForecastRecyclerView.layoutManager=  LinearLayoutManager(context).apply { isAutoMeasureEnabled = false
              orientation=LinearLayoutManager.HORIZONTAL}

                currentCityTemperature.text = response.current.temp.toString()


                val isNight = response.current.getNight()


                context?.let { context ->
                    isNight?.let { isNight ->
                        val imagePath =
                            viewModel.setWeatherIcon(
                                response.current.weather[0].id,
                                isNight,
                                resources
                            ) + ".png"
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