package com.example.putinsurance

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.ui.main.StateViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

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

        // Splitting a string with same form as coordinates in shared preferences
        val coordinates = "59.91-10.75".split("-")
        val lat = coordinates[0].toDouble()
        val lng = coordinates[1].toDouble()

        // Add a marker in Oslo and move the camera
        val oslo = LatLng(lat, lng)
        googleMap.addMarker(MarkerOptions().position(oslo).title("Marker in Oslo"))
        // Zoom in at a specific level
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oslo, 13F))
    }

    //private lateinit var stateViewModel: StateViewModel

    //private val stateViewModel : StateViewModel by viewModels()
    //private lateinit var stateViewModel: StateViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val factory = InjectorUtils.provideTabItemViewModelFactory(this.requireContext())
        //stateViewModel = ViewModelProvider(this,factory).get(StateViewModel::class.java)
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        /*stateViewModel.locText.observe(viewLifecycleOwner, Observer<String> {

        })*/
    }
}