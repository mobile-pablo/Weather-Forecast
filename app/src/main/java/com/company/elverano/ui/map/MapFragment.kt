package com.company.elverano.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.company.elverano.R
import com.company.elverano.data.openWeather.OpenWeatherResponse
import com.company.elverano.databinding.FragmentMapBinding
import com.company.elverano.ui.MainActivity
import com.company.elverano.utils.DummyData
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!

    private val TAG = MapFragment::class.java.simpleName
    private lateinit var map: Map
    private var mapView: AndroidXMapFragment? = null

    private val viewModel by viewModels<MapViewModel>()
    var place = OpenWeatherResponse(
        lat = DummyData.dummy_krakow.lat,
        lon = DummyData.dummy_krakow.lon
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        binding.mapProgressBar.visibility = View.VISIBLE

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView?.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = childFragmentManager.findFragmentById(R.id.here_map_view) as AndroidXMapFragment

        viewModel.weatherResponse.observe(viewLifecycleOwner) {
            it?.let {
                place = it
                loadMapScene()
            }
        }
    }

    companion object {
        const val ZOOM_LVL = 11.0
    }

    private fun loadMapScene() {
        mapView!!.init { error ->
            if (error == OnEngineInitListener.Error.NONE) {
                map = mapView?.map!!
                map.setCenter(GeoCoordinate(place.lat!!, place.lon!!), Map.Animation.NONE)
                map.zoomLevel = ZOOM_LVL

                setUpTheme(map)
                binding.mapProgressBar.visibility = View.INVISIBLE
            } else {
                Log.d("HERE-Maps", "Cannot initialize Map Fragment!")
            }
        }
    }

    private fun setUpTheme(map: Map) {
        val mainActivity = activity as MainActivity
        if (mainActivity.delegate.localNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            val nightScheme =
                map.createCustomizableScheme("nightScheme", Map.Scheme.NORMAL_NIGHT)
            nightScheme?.let { map.setMapScheme(it) }
        } else {
            val dayScheme =
                map.createCustomizableScheme("dayScheme", Map.Scheme.NORMAL_DAY)
            dayScheme?.let { map.setMapScheme(it) }
        }
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
}