package com.example.putinsurance.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.MainActivity
import com.example.putinsurance.R
import com.example.putinsurance.viewmodels.TabViewModel
import com.example.putinsurance.adapters.SectionsStateAdapter
import com.example.putinsurance.utils.InjectorUtils
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_tab.*

class TabFragment : Fragment() {

    private lateinit var tabViewModel : TabViewModel
    private lateinit var sectionsStateAdapter : SectionsStateAdapter
    private lateinit var viewPager2 : ViewPager2

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
        val numOfTabs = tabViewModel.getNumOfTabs()

        createTabs(view, numOfTabs)

        setUpPageChangeListener()

        setUpSwitchButton(view)

        if (numOfTabs >= 5)
            fab.backgroundTintList =
                ColorStateList.valueOf(
                    ContextCompat
                        .getColor(requireActivity(), R.color.colorSecondaryVariant))


    }

    private fun createTabs(view : View, numOfTabs : Int) {
        sectionsStateAdapter = SectionsStateAdapter(numOfTabs, this)
        viewPager2 = view.findViewById(R.id.view_pager)
        viewPager2.adapter = sectionsStateAdapter

        //sectionsStateAdapter.notifyDataSetChanged()

        // Finding tab layout
        // Got a findViewById(R.id.tabs) must not be null. Is there a race condition somewhere??
        val tabs: TabLayout = view.findViewById(R.id.tabs)
        val tabTitles = List(numOfTabs){it}
        Log.d("numOfTabs", "$numOfTabs")

        // Need to do this instead:</LinearLayout>
        TabLayoutMediator(tabs, viewPager2) { tab, position ->
            tab.text = tabTitles[position].toString()
            viewPager2.setCurrentItem(tab.position, true)
            Log.d("Map - tablayoutmediator", "position is $position")
        }.attach()



        //viewPager2.currentItem = numOfTabs - 1

    }

    private fun setUpPageChangeListener() {

        // trying to communicate which page we are on
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabViewModel.setTab(position) // not sure if best practice
            }
        })

    }

    private fun setUpSwitchButton(view: View) {
        val mySwitch : SwitchCompat = view.findViewById(R.id.mySwitch)


        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("TabFragment", "Showing map")
                (activity as MainActivity).showMap(1)
            } else {
                Log.d("TabFragment", "Showing photo")
                (activity as MainActivity).showPhoto(1)
            }
        }


        if (mySwitch.isChecked) {
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