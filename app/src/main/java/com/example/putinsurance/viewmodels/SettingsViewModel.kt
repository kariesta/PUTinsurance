package com.example.putinsurance.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    val password = MutableLiveData<String>(null)
}