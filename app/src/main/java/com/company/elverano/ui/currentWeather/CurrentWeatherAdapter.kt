package com.company.elverano.ui.currentWeather


import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.company.elverano.data.openWeather.OpenWeatherHourly
import com.company.elverano.databinding.ForecastItemBinding
import com.company.elverano.readAsset
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

class CurrentWeatherAdapter(
    private val lists: ArrayList<OpenWeatherHourly>,
    private val offset: Int,
    private val viewModel: CurrentWeatherViewModel,
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
                forecastItemTemperature.text =  String.format("%.1f", openWeatherHourly.temp)
                val date = Date(openWeatherHourly.dt * 1000 + offset * 1000)
                val dateFormat = SimpleDateFormat("YYYY-MM-dd")
                val hourFormat = SimpleDateFormat("hh a")
                forecastCityHour.text = hourFormat.format(date)
                forecastCityDay.text = getWeekDayName(dateFormat.format(date))

               val isNight = openWeatherHourly.getNight()


                binding.root.context?.let { context ->
                    isNight?.let { isNight ->
                        val imagePath =
                            viewModel.setWeatherIcon(openWeatherHourly.weather[0].id, isNight,res) + ".webp"
                        val x = readAsset(context, imagePath)
                        forecastCityImage.setImageBitmap(x)
                    }
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