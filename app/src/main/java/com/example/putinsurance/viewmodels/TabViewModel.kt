package com.example.putinsurance.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.putinsurance.data.DataRepository


class TabViewModel(private val dataRepository: DataRepository): ViewModel() {

    //variables
    var index : MutableLiveData<Int> = MutableLiveData()

    /*fun getLocation(id : Int) : String {
        val claim = dataRepository.getClaimDataFromSharedPrefrences(id)
        return claim.claimLocation
    }*/

    // THE MISTAKE: I created a new MutableLiveData object instead of just updating the value
    // BEWARE
    fun setIndex(ind: Int) {
        Log.d("Map - TabViewModel", "setIndex($ind)")
        index.value = ind
    }

}