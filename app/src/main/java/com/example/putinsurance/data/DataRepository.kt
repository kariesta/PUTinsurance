package com.example.putinsurance.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.putinsurance.Claim
import com.example.putinsurance.TabActivity
import org.json.JSONObject

class DataRepository {
    private val ip = "10.0.2.2"
    private val port = "8080"
    private val urlBase = "http://$ip:$port/"
    private var queue : RequestQueue? = null


    //user id, stuff for login
    fun userValidation(email: String, passHash: String, preferences: SharedPreferences, context: Context): Boolean{
        var success = false
        try {
            success =  validateUserBySharedPreferences(email, passHash, preferences)
        } catch (e : NullPointerException) {
            success = validateUserByServer(email, passHash, preferences, context)
        }
        return success
    }

    fun getUserId(preferences: SharedPreferences): String?{
        return preferences.getString("personID",null)
    }

    // TODO: check if it is really necessary to validate by sharedpref as all user data is deleted when user logs out
    private fun validateUserBySharedPreferences(email: String, passHash: String, preferences: SharedPreferences): Boolean {

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

    private fun validateUserByServer(email : String, passHash : String, preferences: SharedPreferences, context: Context): Boolean {

        // url
        val parameters = "em=$email&ph=$passHash"
        val url = "${urlBase}methodPostRemoteLogin?$parameters"

        return sendPostRequest(url, preferences, context)

    }

    private fun sendPostRequest(url : String, preferences: SharedPreferences, context: Context ): Boolean {
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
                insertIntoSharedPreferences(email, passHash, personID, preferences)

                Log.d("logIn", "SERVER: SUCCESS. SEND TO NEXT ACTIVITY (email: $email and passHash: $passHash)")
                startActivity(context,Intent(context, TabActivity::class.java), null)
            },
            {

                // TODO: check if due to incorrect password or no contact with server (network/server down)
                Log.d("logIn", "SERVER: FAILED TO CONNECT")
            })


        queue?.add(jsonRequest)
        return false //TODO more stable solution.
    }

    private fun insertIntoSharedPreferences(email : String, passHash: String, personID: String, preferences: SharedPreferences) {
        preferences.edit().apply {
            putString("email", email)
            putString("passHash", passHash)
            putString("personID", personID)
            // According to stack overflow, apply is faster than commit as it is asynchronous: https://stackoverflow.com/questions/5960678/whats-the-difference-between-commit-and-apply-in-sharedpreferences
            apply()
        }

    }

    //delete all data on logout
    fun deleteFromSharedPreferences(preferences: SharedPreferences) {
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
    fun getNumberOfClaimsFromSharedPrefrences(preferences: SharedPreferences): Int{
        return preferences.getInt("numberOfClaims",0)
    }

    //get each claimField by id/get all claimfields for one id
    fun getClaimDataFromSharedPrefrences(id:Int, preferences: SharedPreferences): Claim{
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return Claim(preferences.getString("claimID$id",""), preferences.getString("claimDes$id",""),preferences.getString("claimPhoto$id",""),preferences.getString("claimLocation$id",""))
    }

    //get all claims
    fun getAllClaimsFromSharedPrefrences(preferences: SharedPreferences): MutableList<Claim>{
        val numbOfClaims = preferences.getInt("numberOfClaims",0)
        //Log.d("SHAREDPREF","this is now full of $numbOfClaims claims")

        if (numbOfClaims == 0){
            return mutableListOf()
        }
        //lag "kort" for hver claim.
        return (0 until numbOfClaims).map { i: Int ->
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            Claim(preferences.getString("claimID$i",""),preferences.getString("claimDes$i",""),preferences.getString("claimPhoto$i",""),preferences.getString("claimLocation$i",""))
        }.toMutableList()
    }

    //takes a call back method that adds the values to shared preference.
    //TODO WHAT IF NEW CLAIM IS MADE OR EDITED BEFORE FETCHED CLAIM FROM SERVER? A status number to compare versions? a conflicting claims activity?
    fun getAllClaimsFromServer(preferences: SharedPreferences,context: Context){
        //send the request
        val personId = getUserId(preferences)
        val parameters =  "id=$personId"
        val url = "${urlBase}getMethodMyClaims?$parameters"
        sendGetRequest(url, preferences,context)
    }

    fun sendGetRequest(url: String,preferences: SharedPreferences,context: Context){
        // TODO: Check if we can only have one queue per activity, same as 68
        queue = Volley.newRequestQueue(context)
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("GET_CLAIMS", "SERVER: SUCCESS. ServerClaims put in sharedpref")
                insertServerClaimsIntoSharedPref(response)
            },
            {
                // TODO: check if due to incorrect password or no contact with server (network/server down)
                Log.d("GET_CLAIMS", "SERVER: FAILED DUE TO: ${it.message}")
            })

        queue?.add(jsonRequest)
    }

    fun insertServerClaimsIntoSharedPref(serverClaimsResponse: JSONObject){
        // serverResponse: D/GET_CLAIMS: {"id":"0","numberOfClaims":"2","claimId":["0","1","na","na","na"],"claimDes":["desc00","desc01","na","na","na"],"claimPhoto":["phot00","phot01","na","na","na"],"claimLocation":["50-10","50-15","na","na","na"],"claimStatus":["na","na","na","na","na"]}
        val numOfClaims = serverClaimsResponse.getString("numberOfClaims")
        for (0..numOfClaims):
        /*preferences.edit().apply{
            putInt("numberOfClaims", numbOfClaims + 1)
            putString("claimID$numbOfClaims", numbOfClaims.toString())
            putString("claimDes$numbOfClaims", descString)
            putString("claimPhoto$numbOfClaims", "$photoName${numbOfClaims}.jpg")
            putString("claimLocation$numbOfClaims", "$longString-$latString")
            apply()
        }*/

        // TODO WAS HERE; ADD TO SHAREDPREF

    }


    //add new claim, sharedpref, and try to send to server
    fun addClaim(numbOfClaims: Int,
                 descString: String,
                 longString: String,
                 latString: String,
                 photoName: String,
                 preferences: SharedPreferences,
                 context: Context
    ){
        insertClaimIntoSharedPreferences(numbOfClaims, descString, longString, latString, photoName,preferences)
        val personID = preferences.getString("personID",null)
        //TODO  let this run asyncroniously
        sendClaimToServer(numbOfClaims, descString, longString, latString, photoName,personID,context)
    }

    private fun insertClaimIntoSharedPreferences(
        numbOfClaims: Int,
        descString: String,
        longString: String,
        latString: String,
        photoName: String,
        preferences: SharedPreferences
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
        personID: String,
        context: Context
    ){
        val status = "na"
        //public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
        queue = Volley.newRequestQueue(context)
        val parameters = "userId=$personID&indexUpdateClaim=$claimID&newClaimDes=$descString&newClaimPho=$photoName&newClaimLoc=$longString-$latString&newClaimSta=$status"
        val url = "http://$ip:$port/postInsertNewClaim?$parameters"
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { _ ->
                //response to successful request
                Log.d("ADD_CLAIM", "SERVER: SUCCESS.$,")
            },
            //response to unsuccessful request
            { error  -> Log.d("ADD_CLAIM", "SERVER: FAILED TO CONNECT WITH $url")
                Log.d("ADD_CLAIM", "SERVER: FAILED DUE TO ${error.message}")
                //handle error if server down, note that not connected and put in que again? maybe some resting time.
                //handle error if network low.
            }
        )
        queue?.add(stringRequest)
    }


    //DO to edited claims what is done for new claims.


    //TODO get current location, useful for add claim

}