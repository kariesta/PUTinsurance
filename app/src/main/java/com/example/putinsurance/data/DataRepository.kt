package com.example.putinsurance.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.putinsurance.R
import com.example.putinsurance.TabActivity

class DataRepository constructor(val context: Context, val preferences: SharedPreferences) {
    private val ip = "10.0.2.2"
    private val port = "8080"
    private var queue : RequestQueue? = null

    //todo value to store weither we are synced with server.

    //user id, stuff for login
    fun userValidation(email: String, passHash: String, view: View): Boolean{
        var success: Boolean
        try {
            success =  validateUserBySharedPreferences(email, passHash)
        } catch (e : NullPointerException) {
            success = validateUserByServer(email, passHash,view)
        }
        return success
    }

    // TODO: check if it is really necessary to validate by sharedpref as all user data is deleted when user logs out
    private fun validateUserBySharedPreferences(email: String, passHash: String): Boolean {

        val em = preferences.getString("email", null)
        val ph = preferences.getString("passHash", null)

        if (em!! == email && ph!! == passHash) {
            Log.d("logIn", "SHARED PREFS: SUCCESS. SEND TO NEXT ACTIVITY")
            return true
        } else {
            // TODO: Show to user that password is incorrect
            Log.d("logIn", "SHARED PREFS: FAIL. EMAIL/PASSWORD IS INCORRECT")
            return false
        }
    }

    private fun validateUserByServer(email : String, passHash : String, view: View): Boolean {

        // url
        val parameters = "em=$email&ph=$passHash"
        val url = "http://$ip:$port/methodPostRemoteLogin?$parameters"

        return sendPostRequest(url, view)

    }

    private fun sendPostRequest(url : String, view: View): Boolean {
        // Request queue
        // TODO: Check if we can only have one queue per activity
        queue = Volley.newRequestQueue(context)
        // jsonRequest
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST, url, null,
            { response ->
                // Parsing JSON object
                val email = response.getString("email")
                val passHash = response.getString("passHash")
                val personID = response.getString("id")

                // Updating shared preferences
                insertIntoSharedPreferences(email, passHash, personID)

                Log.d("logIn", "SERVER: SUCCESS. SEND TO NEXT ACTIVITY (email: $email and passHash: $passHash)")
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_blankFragment)
                //startActivity(context,Intent(context, TabActivity::class.java), null)
            },
            {

                // TODO: check if due to incorrect password or no contact with server (network/server down)
                Log.d("logIn", "SERVER: FAILED TO CONNECT")
            })


        queue?.add(jsonRequest)
        return false //TODO more stable solution.
    }

    private fun insertIntoSharedPreferences(email : String, passHash: String, personID: String) {
        preferences.edit().apply {
            putString("email", email)
            putString("passHash", passHash)
            putString("personID", personID)
            // According to stack overflow, apply is faster than commit as it is asynchronous: https://stackoverflow.com/questions/5960678/whats-the-difference-between-commit-and-apply-in-sharedpreferences
            apply()
        }

    }

    //delete all data on logout
    fun deleteFromSharedPreferences() {
        preferences.edit().apply {
            remove("email")
            remove("passHash")
            remove("personID")

            //val numbOfClaims = preferences.getInt("numberOfClaims",0)
            clear()


            apply()
        }
    }

    //check for updates from server, use on login

    //get number of claims
    fun getNumberOfClaims(): Int{
        return preferences.getInt("numberOfClaims",0)
    }

    //get each claimField by id/get all claimfields for one id
    fun getClaimData(id:Int ): Claim {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return Claim(preferences.getString("claimID$id",""), preferences.getString("claimDes$id",""),preferences.getString("claimPhoto$id",""),preferences.getString("claimLocation$id",""))
    }

    //get all claims
    fun getAllClaims(): MutableList<Claim>{
        val numbOfClaims = preferences.getInt("numberOfClaims",0)
        //Log.d("SHAREDPREF","this is now full of $numbOfClaims claims")

        if (numbOfClaims == 0){
            return mutableListOf()
        }
        //lag "kort" for hver claim.
        return (0 until numbOfClaims).map { i: Int ->
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            (Claim(
        preferences.getString("claimID$i", ""),
        preferences.getString("claimDes$i", ""),
        preferences.getString("claimPhoto$i", ""),
        preferences.getString("claimLocation$i", "")
    ))
        }.toMutableList()
    }


    //add new claim, sharedpref, and try to send to server

    fun addClaim(numbOfClaims: Int,
                 descString: String,
                 longString: String,
                 latString: String,
                 photoName: String
    ){
        insertClaimIntoSharedPreferences(numbOfClaims, descString, longString, latString, photoName)
        val personID = preferences.getString("personID",null)
        //TODO  let this run asyncroniously
        sendClaimToServer(numbOfClaims, descString, longString, latString, photoName,personID)
    }

    private fun insertClaimIntoSharedPreferences(
        numbOfClaims: Int,
        descString: String,
        longString: String,
        latString: String,
        photoName: String
    ){
        preferences.edit().apply{
            putInt("numberOfClaims", numbOfClaims + 1)
            putString("claimID$numbOfClaims", numbOfClaims.toString())
            putString("claimDes$numbOfClaims", descString)
            putString("claimPhoto$numbOfClaims", "$photoName${numbOfClaims}.jpg")
            putString("claimLocation$numbOfClaims", "$longString-$latString")
            apply()
        }
    }

    private fun sendClaimToServer(
        claimID: Int,
        descString: String,
        longString: String,
        latString: String,
        photoName: String,
        personID: String
    ){
        val status = "na"
        //public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
        queue = Volley.newRequestQueue(context)
        val parameters = "userId=$personID&indexUpdateClaim=$claimID&newClaimDes=$descString&newClaimPho=$photoName&newClaimLoc=$longString-$latString&newClaimSta=$status"
        val url = "http://$ip:$port/postInsertNewClaim?$parameters"
        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, null,
            {
                Log.d("ADD_CLAIM", "SERVER: SUCCESS.$it")
            },
            {
                Log.d("ADD_CLAIM", "SERVER: FAILED TO CONNECT WITH $url, $it")
                //TODO: try again later, remember to add imagefile, not filename
            }
        )
        queue?.add(jsonRequest)

    }


    //TODO get current location, useful for add claim

}