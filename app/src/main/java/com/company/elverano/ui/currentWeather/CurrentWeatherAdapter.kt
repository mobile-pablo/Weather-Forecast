package com.company.elverano.ui.currentWeather


import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.company.elverano.data.openWeather.OpenWeatherHourly
import com.company.elverano.databinding.ForecastItemBinding
import com.company.elverano.utils.setWeatherIcon
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class CurrentWeatherAdapter(
    private val lists: ArrayList<OpenWeatherHourly>,
    private val offset: Int,
    private val res: Resources,
) : RecyclerView.Adapter<CurrentWeatherAdapter.LocalViewHolder>() {
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
                forecastItemTemperature.text =
                    String.format("%.1f", openWeatherHourly.temp).replace(",", ".")
                val date = Date(openWeatherHourly.dt * 1000 + offset * 1000)
                val dateFormat = SimpleDateFormat("YYYY-MM-dd")
                val hourFormat = SimpleDateFormat("hh a")
                forecastCityHour.text = hourFormat.format(date)
                forecastCityDay.text = getWeekDayName(dateFormat.format(date))

                val isNight = openWeatherHourly.getNight()

                isNight?.let { isNight ->
                    val drawable =
                        setWeatherIcon(openWeatherHourly.weather[0].id, isNight, res)
                    Glide.with(itemView)
                        .load(drawable)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(forecastCityImage)
                }

            }
        }
    }


    fun getWeekDayName(s: String?): String? {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val date = LocalDate.parse(s)
            val day: DayOfWeek = date.dayOfWeek
            return day.toString()
        } else {
            return s
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }


}