package com.example.putinsurance.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.putinsurance.R

class PreferencesFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}