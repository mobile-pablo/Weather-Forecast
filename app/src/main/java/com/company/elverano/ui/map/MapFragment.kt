package com.company.elverano.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.company.elverano.R
import com.company.elverano.databinding.FragmentMapBinding
import com.company.elverano.utils.DummyData
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.MapStyle
import com.here.sdk.mapviewlite.MapViewLite


class MapFragment : Fragment(R.layout.fragment_map) {
    private var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!
    private val TAG = MapFragment::class.java.simpleName
    private var permissionsRequestor: PermissionsRequestor? = null
    private var mapView: MapViewLite? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater,container,false)

        mapView = binding.hereMapView
        mapView!!.onCreate(savedInstanceState)
        handleAndroidPermissions()

        return binding.root

    }

    private fun handleAndroidPermissions() {
        permissionsRequestor = PermissionsRequestor(requireActivity())
        permissionsRequestor!!.request(object : PermissionsRequestor.ResultListener {
            override fun permissionsGranted() {
                loadMapScene()
            }

            override fun permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.")
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        permissionsRequestor?.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView!!.mapScene.loadScene(
            MapStyle.NORMAL_DAY
        ) { errorCode ->
            if (errorCode == null) {
                val place =DummyData.dummy_krakow
                mapView!!.camera.target = GeoCoordinates(place.lat,  place.lon)
                mapView!!.camera.zoomLevel = 14.0
            } else {
                Log.d(TAG, "onLoadScene failed: $errorCode")
            }
        }
    }

     override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

     override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

     override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }
}