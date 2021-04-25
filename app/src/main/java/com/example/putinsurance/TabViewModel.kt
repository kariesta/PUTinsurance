package com.example.putinsurance

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// private val dataRepository: DataRepository
class TabViewModel: ViewModel() {

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