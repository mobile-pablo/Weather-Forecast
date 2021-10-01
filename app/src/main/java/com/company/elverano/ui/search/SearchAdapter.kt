package com.company.elverano.ui.search

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.databinding.HistoryItemBinding
import java.util.*
import kotlin.collections.ArrayList

class SearchAdapter(
    private val weatherLists: ArrayList<OpenWeatherResponse>,
    private val nameLists : ArrayList<String>,
    private val offset: Int,
    private val res: Resources,
    private val viewModel: SearchViewModel
) : RecyclerView.Adapter<SearchAdapter.LocalViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val binding =
            HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val item = weatherLists[position]
        val name = nameLists[position]
        holder.onBind(item,name)

    }

    inner class LocalViewHolder(private val binding: HistoryItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun onBind(openWeatherResponse: OpenWeatherResponse, name:String) {
            binding.apply {
                historyCityName.text= name
                historyCityMain.text = openWeatherResponse.current.weather[0].main
                historyCityTemp.text = openWeatherResponse.current.temp.toString()

                val isNight = openWeatherResponse.current.getNight()

                isNight?.let { isNight ->
                    val drawable =
                        viewModel.setWeatherIcon(openWeatherResponse.current.weather[0].id, isNight, res)
                    Glide.with(itemView)
                        .load(drawable)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(historyCityImg)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return weatherLists.size
    }


}