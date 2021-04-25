package com.example.putinsurance.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.putinsurance.data.Claim
import com.example.putinsurance.data.DataRepository

class StateViewModel(private val dataRepository: DataRepository) : ViewModel() {

    private val _index = MutableLiveData<Int>()
    /*val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }
*/
    private val _loc = MutableLiveData<String>()
    val locText: LiveData<String> = _loc

    private val _desc = MutableLiveData<String>()
    val descText: LiveData<String> = _desc

    private val _id = MutableLiveData<String>()
    val idText: LiveData<String> = Transformations.map(_id) {
        "ID: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
        val claim = dataRepository.getClaimDataFromSharedPrefrences(5 - index)
        Log.d("PICK_TAB", claim.toString())
        _loc.value = claim.claimLocation
        _desc.value = claim.claimDes
        _id.value = claim.claimID
    }
}