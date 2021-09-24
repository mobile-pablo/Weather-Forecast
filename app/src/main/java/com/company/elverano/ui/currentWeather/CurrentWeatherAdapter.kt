package com.company.elverano.ui.currentWeather


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.elverano.data.openWeather.OpenWeatherHourly
import com.company.elverano.databinding.ForecastItemBinding
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class CurrentWeatherAdapter(
    private val lists: ArrayList<OpenWeatherHourly>,
    private val offset: Int
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
                forecastItemTemperature.text = openWeatherHourly.temp.toString()
                val date = Date(openWeatherHourly.dt * 1000 + offset * 1000)
                val sdf = SimpleDateFormat("YYYY-MM-dd")
                forecastCityHour.text = getWeekDayName(sdf.format(date))
                forecastCityDay.text = date.day.toString()
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