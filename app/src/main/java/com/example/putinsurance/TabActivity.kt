package com.example.putinsurance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.appcompat.app.AppCompatActivity
import com.example.putinsurance.ui.main.SectionsStateAdapter
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_tab.*

class TabActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        // Adapter
        val sectionsStateAdapter = SectionsStateAdapter(this)
        val viewPager2: ViewPager2 = findViewById(R.id.view_pager)
        viewPager2.adapter = sectionsStateAdapter

        // Finding tab layout
        // Got a findViewById(R.id.tabs) must not be null. Is there a race condition somewhere??
        val tabs: TabLayout = findViewById(R.id.tabs)

        val tabTitles = listOf("4", "3", "2", "1", "0")

        // Does not work with viewpager2:
        //tabs.setupWithViewPager(viewPager2)

        // Need to do this instead:</LinearLayout>
        TabLayoutMediator(tabs, viewPager2) {
            tab, position -> tab.text = tabTitles[position]
            viewPager2.setCurrentItem(tab.position, true)
        }.attach()

    }

    fun newClaim(view: View) {
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //    .setAction("Action", null).show()

        startActivity(Intent(this, MainActivity::class.java))
    }

    // According to this answer, fragment switches should always be done through the activity in which they reside:
    // https://stackoverflow.com/questions/58891060/android-switch-between-multiple-fragments-in-a-tab
    // According to this blog post you should hide and show the fragments, especially since map fragment is expensive to set up
    // https://medium.com/sweet-bytes/switching-between-fragments-without-the-mindless-killing-spree-9efee5f51924
    // Only works on one tab -> might have to send in the number of the tab to create a unique id.
    // However, stops working on the one tab after opening a few other tabs.
    fun showMap(position: Int?) {
        Log.d("tab", "Showing map")

        val mapTag = "map_$position"
        val photoTag = "photo_$position"

        Log.d("tab", mapTag)
        Log.d("tab", photoTag)

        if (supportFragmentManager.findFragmentByTag(mapTag) != null) {
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
        }
    }

    fun showPhoto(position: Int?) {
        Log.d("tab", "Showing photo")

        val mapTag = "map_$position"
        val photoTag = "photo_$position"

        // will not work on first switch as map is not added yet.
        if (supportFragmentManager.findFragmentByTag(mapTag) != null) {
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mapTag)!!)
                .commit()
        }

        //supportFragmentManager.beginTransaction().add(R.id.frameLayout)

        val image : ImageView = findViewById(R.id.imageView)
        imageView.bringToFront()
    }

    // OnCheckedChangeListener is recommended by stack overflow:
    // https://stackoverflow.com/questions/11278507/android-widget-switch-on-off-event-listener


}