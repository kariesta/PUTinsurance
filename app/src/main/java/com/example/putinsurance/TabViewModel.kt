package com.example.putinsurance

import androidx.lifecycle.ViewModel
import com.example.putinsurance.data.DataRepository

class TabViewModel(private val dataRepository: DataRepository): ViewModel() {

    //variables

    fun getLocation(id : Int) : String {
        val claim = dataRepository.getClaimDataFromSharedPrefrences(id)
        return claim.claimLocation
    }

}