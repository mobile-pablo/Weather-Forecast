package com.company.elverano.ui.currentWeather


import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.company.elverano.data.openWeather.OpenWeatherHourly
import com.company.elverano.databinding.ForecastItemBinding
import com.company.elverano.utils.fadeIn
import com.company.elverano.utils.formatDoubleString
import com.company.elverano.utils.setWeatherIcon
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class CurrentWeatherAdapter(
    private val lists: List<OpenWeatherHourly>,
    private val offset: Int,
    private val res: Resources,
) : RecyclerView.Adapter<CurrentWeatherAdapter.LocalViewHolder>() {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val hourFormat = SimpleDateFormat("hh a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val binding =
            ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val item = lists[position]
        holder.onBind(item)
    }

    inner class LocalViewHolder(private val binding: ForecastItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun onBind(openWeatherHourly: OpenWeatherHourly) {

            binding.apply {
                forecastItemTemperature.text = formatDoubleString(openWeatherHourly.temp!!, 1)

                val date = Date(openWeatherHourly.dt!! * 1000 + offset * 1000)

                forecastCityHour.text = hourFormat.format(date)
                forecastCityDay.text = getWeekDayName(dateFormat.format(date))

                val isNight = openWeatherHourly.getNight()
                isNight?.let { night ->
                    val drawable =
                        setWeatherIcon(
                            openWeatherHourly.weather!![0].id!!,
                            night,
                            res
                        )

                    Glide.with(itemView)
                        .load(drawable)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(forecastCityImage)
                }

                animateText(this)
            }
        }
    }

    private fun animateText(binding: ForecastItemBinding) {
        binding.apply {
            forecastItemTemperature.fadeIn()
            forecastItemMeasure.fadeIn()
            forecastCityDay.fadeIn()
            forecastCityHour.fadeIn()
        }
    }

    fun getWeekDayName(s: String?): String? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val date = LocalDate.parse(s)
            val day: DayOfWeek = date.dayOfWeek
            day.toString()
        } else {
            s
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }
}