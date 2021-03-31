package com.example.putinsurance

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.appcompat.app.AppCompatActivity
import com.example.putinsurance.ui.main.SectionsStateAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        // Adapter
        val sectionsStateAdapter = SectionsStateAdapter(this)
        val viewPager2: ViewPager2 = findViewById(R.id.view_pager)
        viewPager2.adapter = sectionsStateAdapter

        // Finding tab layout
        val tabs: TabLayout = findViewById(R.id.tabs)

        val tabTitles = listOf("0", "1", "2", "3", "4")

        // Does not work with viewpager2:
        //tabs.setupWithViewPager(viewPager2)

        // Need to do this instead:</LinearLayout>
        TabLayoutMediator(tabs, viewPager2) {
            tab, position -> tab.text = tabTitles[position]
            viewPager2.setCurrentItem(tab.position, true)
        }.attach()


        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}