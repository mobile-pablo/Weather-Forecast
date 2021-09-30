package com.company.elverano.ui.map

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.company.elverano.R
import com.company.elverano.databinding.FragmentMapBinding
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.common.MapSettings
import com.here.android.mpa.common.OnEngineInitListener
import com.here.android.mpa.mapping.AndroidXMapFragment
import com.here.android.mpa.mapping.Map
import kotlinx.android.synthetic.main.fragment_map.view.*
import java.io.File


class MapFragment: Fragment(R.layout.fragment_map) {
private var _binding: FragmentMapBinding?=null
    val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)
        initialize(50.041913, 21.995905)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


    private var map: Map? = null

    // map fragment embedded in this activity
    private var mapFragment: AndroidXMapFragment? = null

    private fun initialize(lat: Double, lon: Double) {
        // Search for the map fragment to finish setup by calling init().
        mapFragment = childFragmentManager.findFragmentById(R.id.here_map_fragment) as AndroidXMapFragment

        // Set up disk map cache path for this application
        // Use path under your application folder for storing the disk cache

        MapSettings.setDiskCacheRootPath("${context?.externalCacheDir}${File.separator}.here-maps")

        mapFragment!!.init {
            OnEngineInitListener { error ->
                if(error == OnEngineInitListener.Error.NONE){
                    println("Map is ready!")
                    map = mapFragment!!.map

                    map?.setCenter( GeoCoordinate(lat,lon, 0.0),  Map.Animation.NONE)
                    map?.let {
                        it.zoomLevel = (it.maxZoomLevel + it.minZoomLevel) / 2;
                    }

                }else{
                    println("ERROR: Cannot initialize Map Fragment")
                }
            }
        }
    }
}