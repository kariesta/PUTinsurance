package com.example.putinsurance.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class TabItemViewModel : ViewModel() {

    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    private val _loc = MutableLiveData<String>()
    val locText: LiveData<String> = Transformations.map(_loc) {
        "Location: $it"
    }

    private val _desc = MutableLiveData<String>()
    val descText: LiveData<String> = Transformations.map(_desc) {
        "Desc: $it"
    }

    private val _id = MutableLiveData<String>()
    val idText: LiveData<String> = Transformations.map(_id) {
        "claimID: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
        //TODO FETCH DATA FROM SHAREPREF


        _loc.value = "${index*2}-${index}"
        _desc.value = "${index*2} desky"
        _id.value = "${index}"
    }
}