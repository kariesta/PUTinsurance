package com.example.putinsurance.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.putinsurance.data.Claim
import com.example.putinsurance.data.DataRepository

class StateViewModel(private val dataRepository: DataRepository) : ViewModel() {

    private val _index = MutableLiveData<Int>()
    /*val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }
*/
   /* private val _loc = MutableLiveData<String>()
    val locText: LiveData<String> = _loc

    private val _desc = MutableLiveData<String>()
    val descText: LiveData<String> = _desc

    private val _id = MutableLiveData<String>()
    val idText: LiveData<String> = Transformations.map(_id) {
        "ID: $it"
    }
*/
    val claim = MutableLiveData<Claim>()


    // According to this: Shared preferences is thread safe, but not process safe
    fun setIndex(index: Int) {
        _index.value = index
        Log.d("TabItem", "setIndex($index)")
        val numOfClaims = dataRepository.getNumberOfClaims()
        val claimID = numOfClaims - index
        claim.value = dataRepository.getClaimDataFromSharedPrefrences(claimID)

    }

    fun updateClaim(c: Claim, imageString : String) {
        //if (c != claim.value || imageString =! ..) // for not updating unnecessary
        dataRepository.updateClaim(c, imageString)
        //this.claim.value = claim
    }
}