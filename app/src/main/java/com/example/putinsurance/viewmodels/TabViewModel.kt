package com.example.putinsurance.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.putinsurance.data.DataRepository


class TabViewModel(private val dataRepository: DataRepository): ViewModel() {

    // variables
    var index : MutableLiveData<Int> = MutableLiveData()
    var location : MutableLiveData<String> = MutableLiveData()

    // for Kari. Observe this variable
    var photo : MutableLiveData<Bitmap> = MutableLiveData()



    // THE MISTAKE: I created a new MutableLiveData object instead of just updating the value
    // BEWARE
    fun setIndex(tab: Int) {
        Log.d("Fetch", "setIndex($tab)")

        // dataRepository.getNumberOfClaims()
        val tabs = 5
        val ind = tabs - tab - 1
        index.value = ind

        // Updating location
        location.value = getLocation(ind)

        // for Kari
        // Updating photo
        photo.value = getPhoto(ind)
    }

    private fun getLocation(id : Int) : String {
        val claim = dataRepository.getClaimDataFromSharedPrefrences(id)
        return claim.claimLocation
    }

    private fun getPhoto(id : Int) : Bitmap? = null

}