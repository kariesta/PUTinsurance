package com.example.putinsurance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.ui.main.SectionsStateAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayoutMediator

class TabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tab, container, false);


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        //val viewmodel : TabViewModel = ViewModelProviders.of()

        // Adapter
        val sectionsStateAdapter = SectionsStateAdapter(this)
        val viewPager2 : ViewPager2 = view.findViewById(R.id.view_pager)
        viewPager2.adapter = sectionsStateAdapter

        // Finding tab layout
        // Got a findViewById(R.id.tabs) must not be null. Is there a race condition somewhere??
        val tabs: TabLayout = view.findViewById(R.id.tabs)

        val tabTitles = listOf("4", "3", "2", "1", "0")

        // Does not work with viewpager2:
        //tabs.setupWithViewPager(viewPager2)

        // Need to do this instead:</LinearLayout>
        TabLayoutMediator(tabs, viewPager2) {
                tab, position -> tab.text = tabTitles[position]
            viewPager2.setCurrentItem(tab.position, true)
        }.attach()

        // does not work correctly inside here.
        // Need to somehow get information of when it has been
        // thought: should maybe just add all the markers at once?
        // then just zoom in to correct one
        val mySwitch : SwitchCompat = view.findViewById(R.id.mySwitch)


        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("TabFragment", "Showing map")
                (activity as MainActivity).showMap(1)
                //(activity as TabActivity).showMap(arguments?.getInt(ARG_SECTION_NUMBER))
            } else {
                Log.d("TabFragment", "Showing photo")
                (activity as MainActivity).showPhoto(1)
                //(activity as TabActivity).showPhoto(arguments?.getInt(ARG_SECTION_NUMBER))
            }
        }

        //mySwitch.isChecked
        //if (mySwitch.isChecked) {
        //    mySwitch.callOnClick() // only changes from unchecked to checked
        //}


        // mySwitch.isChecked = false

        //mySwitch.callOnClick() // only changes from checked to unchecked??

        // mySwitch.isChecked = true

        // TODO: WHY???
        // for some reason this prevents the bug.
       /*if (mySwitch.isChecked) {
           // I don't think this is ever called
            Log.d("TabFragment", "showMap")
            (activity as MainActivity).showMap(1)
        }
        else {
            Log.d("TabFragment", "showPhoto")
            (activity as MainActivity).showPhoto(1)
        }*/

        // I think it's because you need to detach the map fragment or something
        Log.d("TabFragment", "showPhoto")
        (activity as MainActivity).showPhoto(1)




        //addMap()
        //addPhoto()

       //(activity as MainActivity).restart()


    }

    override fun onStop() {
        super.onStop()
        val mySwitch : SwitchCompat? = view?.findViewById(R.id.mySwitch)
        mySwitch?.setOnCheckedChangeListener(null)
        mySwitch?.isChecked = false
    }

    private fun showMap() {
        //val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_container_view) as MapsFragment?
        //mapFragment?.getMapAsync(callback)
        (activity as MainActivity).showMap(1)
    }

    private val callback = OnMapReadyCallback { googleMap ->


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



    fun newClaim(view: View) {
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //    .setAction("Action", null).show()

        //startActivity(Intent(this, MainActivity::class.java))
    }

    // According to this answer, fragment switches should always be done through the activity in which they reside:
    // https://stackoverflow.com/questions/58891060/android-switch-between-multiple-fragments-in-a-tab
    // According to this blog post you should hide and show the fragments, especially since map fragment is expensive to set up
    // https://medium.com/sweet-bytes/switching-between-fragments-without-the-mindless-killing-spree-9efee5f51924
    // Only works on one tab -> might have to send in the number of the tab to create a unique id.
    // However, stops working on the one tab after opening a few other tabs.
    /*fun showMap(position: Int?) {
        Log.d("tab", "Showing map")

        val mapTag = "map_$position"
        val photoTag = "photo_$position"

        Log.d("tab", mapTag)
        Log.d("tab", photoTag)

        *//*if (supportFragmentManager.findFragmentByTag(mapTag) != null) {
            supportFragmentManager
                .beginTransaction()
                .show(supportFragmentManager.findFragmentByTag(mapTag)!!) // this is scary
                .commit()
        } else {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.map, SupportMapFragment(), mapTag) // think this is wrong -> yes, you have just added the standard map. Need to somehow get
                .commit()
        }

        if (supportFragmentManager.findFragmentByTag(photoTag) != null) {
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(photoTag)!!)
                .commit()
        }*//*
    }

    fun showPhoto(position: Int?) {
        Log.d("tab", "Showing photo")

        val mapTag = "map_$position"
        val photoTag = "photo_$position"

        // will not work on first switch as map is not added yet.
        *//*if (supportFragmentManager.findFragmentByTag(mapTag) != null) {
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mapTag)!!)
                .commit()
        }

        //supportFragmentManager.beginTransaction().add(R.id.frameLayout)

        val image : ImageView = findViewById(R.id.imageView)
        imageView.bringToFront()*//*
    }

    // OnCheckedChangeListener is recommended by stack overflow:
    // https://stackoverflow.com/questions/11278507/android-widget-switch-on-off-event-listener
*/

}