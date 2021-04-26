package com.example.putinsurance.fragments

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
import com.example.putinsurance.MainActivity
import com.example.putinsurance.R
import com.example.putinsurance.viewmodels.TabViewModel
import com.example.putinsurance.ui.main.SectionsStateAdapter
import com.example.putinsurance.utils.InjectorUtils
import com.google.android.material.tabs.TabLayoutMediator

class TabFragment : Fragment() {

    private lateinit var tabViewModel : TabViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        //val viewmodel : TabViewModel = ViewModelProviders.of()

        // TODO: check what context should be used
        val factory = InjectorUtils.provideTabViewModelFactory(this.requireActivity())
        tabViewModel = ViewModelProvider(this.requireActivity(), factory).get(TabViewModel::class.java)

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
        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text = tabTitles[position]
            viewPager2.setCurrentItem(tab.position, true)
            Log.d("Map - tablayoutmediator", "position is $position")
        }.attach()

        // trying to communicate which page we are on
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabViewModel.setIndex(position)
            }
        })

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
       if (mySwitch.isChecked) {
           // I don't think this is ever called
            Log.d("Switch", "showMap")
            (activity as MainActivity).showMap(1)
        }
        else {
            Log.d("Switch", "showPhoto")
            (activity as MainActivity).showPhoto(1)
        }

    }

    // Image is loaded even though map is shown
    override fun onStop() {
        super.onStop()
        //(activity as MainActivity).detachBoth()
        //val mySwitch : SwitchCompat? = view?.findViewById(R.id.mySwitch)
        //mySwitch?.setOnCheckedChangeListener(null)
        //mySwitch?.isChecked = false
    }

}