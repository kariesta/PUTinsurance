package com.example.putinsurance.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.putinsurance.data.Claim
import com.example.putinsurance.data.DataRepository


class TabViewModel(private val dataRepository: DataRepository): ViewModel() {

    // variables
    var index : MutableLiveData<Int> = MutableLiveData()
    //var location : MutableLiveData<String> = MutableLiveData()

    // for Kari. Observe this variable
    var photo : MutableLiveData<Bitmap> = MutableLiveData()

    var claim : MutableLiveData<Claim> = MutableLiveData()

    // unsure of how to
    var allClaims : MutableLiveData<MutableList<Claim>> = MutableLiveData()



    // THE MISTAKE: I created a new MutableLiveData object instead of just updating the value
    // BEWARE
    fun setTab(tab: Int, maxTab : Int) {
        Log.d("Fetch", "setIndex($tab)")

        // dataRepository.getNumberOfClaims()
        //val tabs = 5
        val id = maxTab - tab - 1

        claim.value = dataRepository.getClaimDataFromSharedPrefrences(id)
        index.value = id

        /*// Updating location
        location.value = getLocation(id)*/

        // for Kari
        // Updating photo
        photo.value = getPhoto(id)
        Log.d("SHOW_IMAGE", "photo.value now: ${photo.value}")
    }

    fun addClaim() {
        // use notifyDataSetChanged
    }

    /*private fun getLocation(id : Int) : String {
        val claim = dataRepository.getClaimDataFromSharedPrefrences(id)
        return claim.claimLocation
    }*/

    private fun getPhoto(id: Int) : Bitmap? {
        var image = dataRepository.getClaimImageFromPreferences(id)
        if (image == null){
            dataRepository.getClaimImageFromServer(id)
            image = dataRepository.getClaimImageFromPreferences(id)
        }
        return image
    }

}