package com.example.putinsurance.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
// The activity that hosts the adapter
class SectionsStateAdapter(activity: AppCompatActivity) :
    FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return TabFragment.newInstance(position + 1)
    }

    override fun getItemCount(): Int {
        // Show 5 total pages.
        return 5
    }
}