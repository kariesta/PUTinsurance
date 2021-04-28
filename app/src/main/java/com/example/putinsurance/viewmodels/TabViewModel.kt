package com.example.putinsurance.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.putinsurance.data.Claim
import com.example.putinsurance.data.DataRepository


class TabViewModel(private val dataRepository: DataRepository): ViewModel() {

    // variables
    //var index : MutableLiveData<Int> = MutableLiveData()

    // for Kari. Observe this variable
    var photo : MutableLiveData<String> = MutableLiveData()
    var location = MutableLiveData<String>()

    fun getNumOfTabs() =
        dataRepository.getNumberOfClaims()

    fun getClaim(position: Int) =
        dataRepository.getClaimDataFromSharedPrefrences(position - 1)

    fun updateClaim(claim: Claim, imageString : String) {
        dataRepository.updateClaim(claim, imageString)
        notifyChanged(claim)
    }

    fun setTab(position: Int) {
        Log.d("Fetch", "setIndex($position)")

        dataRepository
            .getClaimDataFromSharedPrefrences(position)
            .value?.let { notifyChanged(it) }
    }

    private fun notifyChanged(claim: Claim) {
        if (claim.claimLocation != location.value)
            location.value = claim.claimLocation

        if (claim.claimPhoto != photo.value)
            photo.value = claim.claimLocation
    }


    /*private fun getPhoto(id : Int) : Bitmap? = null*/

}