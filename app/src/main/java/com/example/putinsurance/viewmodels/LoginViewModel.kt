package com.example.putinsurance.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    val email = MutableLiveData<String>("joe@gmail.com")

    val password = MutableLiveData<String>("xpta")

}