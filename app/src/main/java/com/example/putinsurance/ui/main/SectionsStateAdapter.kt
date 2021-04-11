package com.example.putinsurance.ui.main

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
// The activity that hosts the adapter
class SectionsStateAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        Log.d("TABS","sectionStateAdapter lerlori")

        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.d("TABS","sectionStateAdapter END lerlori")
        return TabItemFragment.newInstance(position + 1)
    }

    override fun getItemCount(): Int {
        // Show 5 total pages.
        return 5
    }
}