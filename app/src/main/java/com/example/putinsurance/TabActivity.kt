package com.example.putinsurance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.appcompat.app.AppCompatActivity
import com.example.putinsurance.ui.main.SectionsStateAdapter
import com.google.android.material.tabs.TabLayoutMediator

class TabActivity : AppCompatActivity() {
    val MAX_CLAIMS = 5
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        // Adapter
        val sectionsStateAdapter = SectionsStateAdapter(this)
        val viewPager2: ViewPager2 = findViewById(R.id.view_pager)
        viewPager2.adapter = sectionsStateAdapter

        // Finding tab layout
        val tabs: TabLayout = findViewById(R.id.tabs)

        val tabTitles = listOf("4", "3", "2", "1", "0")

        // Does not work with viewpager2:
        //tabs.setupWithViewPager(viewPager2)
        sharedPref = getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)

        // Need to do this instead:</LinearLayout>
        TabLayoutMediator(tabs, viewPager2) {
            tab, position -> tab.text = tabTitles[position]
            viewPager2.setCurrentItem(tab.position, true)
        }.attach()

    }

    fun newClaim(view: View) {
        val numbOfClaims = sharedPref.getInt("numberOfClaims",0)
        if (numbOfClaims >= MAX_CLAIMS) {
            Toast.makeText(this,"claim limit reached", Toast.LENGTH_SHORT).show()
            return
        }
        //startActivity(Intent(this, ClaimFormActivity::class.java))
    }

    private fun fillTestingPref(sharedPref: SharedPreferences) {
        sharedPref.edit().apply{
            putInt("personID", 0)
            putInt("numberOfClaims", 2)
            putString("claimID0", "0")
            putString("claimID1", "1")
            putString("claimDes0", "desc00")
            putString("claimDes1", "desc01")
            putString("claimPhoto0", "photo0.jpg")

            putString("claimPhoto1", "photo1.jpg")
            putString("claimLocation0", "50-10")
            putString("claimLocation1", "51-15")
            apply()
        }
    }
}
