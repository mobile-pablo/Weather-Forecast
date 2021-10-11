package com.company.elverano.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.company.elverano.R
import com.company.elverano.data.error.CustomError
import com.company.elverano.data.historyWeather.HistoryWeatherResponse
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.databinding.FragmentSearchBinding
import com.company.elverano.utils.ResultEvent
import com.company.elverano.utils.fadeIn
import com.company.elverano.utils.formatDoubleString
import com.company.elverano.utils.setWeatherIcon
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()

    companion object {
        const val RADIUS = 5
        const val SMALL_RADIUS = 1
        const val SAMPLING = 8
        const val SMALL_SAMPLING = 2
        const val ANIM_DURATION = 500
        const val FADE_IN_DURATION: Long = 1500
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)


        initializeObservers()

        addSearchViewListener()


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            collectResults()
        }
    }

    private fun initializeObservers() {
        viewModel.weatherResponse.observe(viewLifecycleOwner) {
            it?.let {
                updateUI(it)
            }
        }

        viewModel.historyResponse.observe(viewLifecycleOwner) {
            it?.let {
                updateHistoryUI(it)
            }
        }
    }

    private fun initializeBlur() {
        Blurry.with(context)
            .radius(RADIUS)
            .sampling(SAMPLING)
            .async().animate(ANIM_DURATION).onto(binding.historyItemOne)

        Blurry.with(context)
            .radius(RADIUS)
            .sampling(SAMPLING)
            .async().animate(ANIM_DURATION).onto(binding.historyItemTwo)

        Blurry.with(context)
            .radius(SMALL_RADIUS)
            .sampling(SMALL_SAMPLING)
            .async().animate(ANIM_DURATION)
            .onto(binding.searchCurrentDetailsBox)
    }

    private suspend fun collectResults() {
        viewModel.resultEvent.collect { event ->
            when (event) {
                is ResultEvent.Success -> {
                    binding.searchProgressBar.visibility = View.INVISIBLE
                    viewModel.deleteError()
                    Log.d("ResultEvent", "Success")
                }

                is ResultEvent.Error -> {
                    binding.searchProgressBar.visibility = View.INVISIBLE
                    viewModel.insertError(CustomError(message = event.message))
                }

            }

            navigateToCurrent()
        }
    }

    private fun navigateToCurrent() {
        val action = SearchFragmentDirections.actionSearchFragmentToCurrentFragment()
        findNavController().navigate(action)
    }

    private fun addSearchViewListener() {
        binding.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {

                    if (query != null) {
                        searchProgressBar.visibility = View.VISIBLE
                        viewModel.searchLocation(query)
                        searchView.setQuery("", false)
                        searchView.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })
        }
    }

    @DelicateCoroutinesApi
    private fun updateUI(weather: OpenWeatherResponse) {
        binding.apply {

            searchCurrentHumidity.text = "${weather.current.humidity} %"
            searchCurrentPressure.text = "${weather.current.pressure}hPa"
            searchCurrentWindSpeed.text = "${weather.current.wind_speed} km/h"
            searchCurrentWindDegrees.text = "${weather.current.wind_deg} ∡"

            searchCurrentHumidity.fadeIn(FADE_IN_DURATION)
            searchCurrentPressure.fadeIn(FADE_IN_DURATION)
            searchCurrentWindSpeed.fadeIn(FADE_IN_DURATION)
            searchCurrentWindDegrees.fadeIn(FADE_IN_DURATION)


            val isNight = weather.current.getNight()
            isNight?.let {
                val drawable =
                    setWeatherIcon(
                        weather.current.weather[0].id,
                        isNight,
                        resources
                    )

                context?.let {
                    Glide.with(it)
                        .load(drawable)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(searchCurrentImg)
                }


            }

            GlobalScope.launch {
                delay(500)
                view?.post {
                    initializeBlur()
                }
            }
        }
    }

    private fun updateHistoryUI(historyResponse: HistoryWeatherResponse) {
        val firstItem = historyResponse.data?.get(0)
        val secondItem = historyResponse.data?.get(1)

        binding.apply {

            firstItem?.let { item->
                historyCityNameOne.text = item.name
                historyCityTempOne.text = formatDoubleString(firstItem.temp, 0)
                historyCityMainOne.text = item.main
                    item.getNight()?.let { night->
                    val drawable =
                        setWeatherIcon(
                            item.weather_id,
                            night,
                            resources
                        )

                    context?.let {
                        Glide.with(it)
                            .load(drawable)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(historyCityImgOne)
                    }

                }

                historyCityNameOne.fadeIn()
                historyCityTempOne.fadeIn()
                historyCityMainOne.fadeIn()
            }

            secondItem?.let { item ->
                historyCityNameTwo.text = item.name
                historyCityTempTwo.text = formatDoubleString(secondItem.temp, 0)
                historyCityMainTwo.text = item.main
                item.getNight()?.let {  night ->
                    val drawable =
                        setWeatherIcon(
                            item.weather_id,
                            night,
                            resources
                        )

                    context?.let {
                        Glide.with(it)
                            .load(drawable)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(historyCityImgTwo)
                    }

                }

                historyCityNameTwo.fadeIn()
                historyCityTempTwo.fadeIn()
                historyCityMainTwo.fadeIn()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}