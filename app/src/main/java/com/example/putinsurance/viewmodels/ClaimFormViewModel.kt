package com.example.putinsurance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class ClaimFormViewModel : ViewModel() {

    val lat = MutableLiveData<String>()

    val long = MutableLiveData<String>()

    val desc = MutableLiveData<String>()

    //setLocation() -> lat = 34.56, long = 56.34

}