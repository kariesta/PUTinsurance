package com.example.putinsurance.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.putinsurance.fragments.TabItemFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
// The activity that hosts the adapter
class SectionsStateAdapter(private val numOfTabs: Int, fragment : Fragment) :
    FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return TabItemFragment.newInstance(position + 1)
    }

    override fun getItemCount(): Int {
        return numOfTabs
    }
}