package com.company.elverano.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.company.elverano.R
import com.company.elverano.databinding.FragmentMapBinding
import com.company.elverano.utils.DummyData
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapviewlite.MapStyle
import com.here.sdk.mapviewlite.MapViewLite
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    private var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!

    private val TAG = MapFragment::class.java.simpleName
    private var permissionsRequestor: PermissionsRequestor? = null
    private var mapView: MapViewLite? = null

    private val viewModel by viewModels<MapViewModel>()
    var place = DummyData.dummy_krakow

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        mapView = binding.hereMapView
        mapView!!.onCreate(savedInstanceState)


        viewModel.weatherResponse.observe(viewLifecycleOwner) {
            place = it
            handleAndroidPermissions()
        }
        return binding.root

    }

    companion object{
        const val ZOOM_LVL =11.0
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
                mapView!!.camera.target = GeoCoordinates(place.lat, place.lon)
                mapView!!.camera.zoomLevel = ZOOM_LVL
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