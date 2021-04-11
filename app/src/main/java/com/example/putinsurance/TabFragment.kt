package com.example.putinsurance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.ui.main.SectionsStateAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TabFragment : Fragment() {
    val MAX_CLAIMS = 5
    private lateinit var sharedPref : SharedPreferences
    private lateinit var viewModel: TabViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TABS","tabFragment lerlori")
        viewModel = ViewModelProvider(this).get(TabViewModel::class.java)
        val root = inflater.inflate(R.layout.tab_fragment, container, false)
        // Adapter
        //val sectionsStateAdapter = SectionsStateAdapter(this.requireActivity())
        Log.d("TABS","etter sectionStateAdapterkall lerlori")
        /*val viewPager2: ViewPager2 = root.findViewById(R.id.view_pager)
        viewPager2.adapter = sectionsStateAdapter //Suspect this leaves a white page

        // Finding tab layout
        val tabs: TabLayout = root.findViewById(R.id.tabs)
        val tabTitles = listOf("4", "3", "2", "1", "0")

        // Does not work with viewpager2:
        //tabs.setupWithViewPager(viewPager2)

        // Need to do this instead:</LinearLayout>
        TabLayoutMediator(tabs, viewPager2) {
                tab, position -> tab.text = tabTitles[position]
            viewPager2.setCurrentItem(tab.position, true)
        }.attach()*/
        Log.d("TABS","tabFragment END lerlori$container   and $savedInstanceState")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("TABS","tabFragment END lerloriand $savedInstanceState")

    }

}
