package com.example.putinsurance.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.R
import com.example.putinsurance.utils.InjectorUtils
import com.example.putinsurance.viewmodels.TabViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var coordinates : String = "59.91-10.75"

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        googleMap.clear()
        setNewMarker(coordinates, googleMap)

    }

    private lateinit var tabViewModel : TabViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // IMPORTANT!! The scope of the viewmodel MUST be the same in the different fragments,
        // or else you will get a different viewmodel
        val factory = InjectorUtils.provideTabViewModelFactory(this.requireActivity())
        tabViewModel = ViewModelProvider(this.requireActivity(), factory).get(TabViewModel::class.java)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        tabViewModel.index.observe(viewLifecycleOwner, Observer<Int> {
            Log.d("Switch", "Observed change in index$it")
            coordinates = "${it*10}-${it*15}"
            mapFragment?.getMapAsync(callback)
        })

    }


    private fun setNewMarker(location : String, googleMap: GoogleMap) {
        // Splitting a string with same form as coordinates in shared preferences
        val coordinates = location.split("-")
        val lat = coordinates[0].toDouble()
        val lng = coordinates[1].toDouble()

        // Add a marker in Oslo and move the camera
        val pos = LatLng(lat, lng)
        googleMap.addMarker(MarkerOptions().position(pos).title("Marker in $location"))
        // Zoom in at a specific level
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 5F))

    }
}