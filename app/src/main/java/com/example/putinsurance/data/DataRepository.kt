package com.example.putinsurance.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.File


class DataRepository private constructor(private val context: Context, private  val preferences: SharedPreferences ) {

    companion object {
        @Volatile private var instance: DataRepository? = null
        const val WIFI = "Wi-Fi"
        const val ANY = "Any"
        var isConnected = false

        fun getInstance(context: Context, preferences: SharedPreferences) = instance ?: synchronized(this) {
            instance?: DataRepository(context, preferences).also { instance = it}
        }
    }

    private val ip = "10.0.2.2"
    private val port = "8080"
    private val urlBase = "http://$ip:$port/"
    private var queue : RequestQueue? =  Volley.newRequestQueue(context)
    private var offlineRequests: MutableList<() -> Unit> = mutableListOf()
    private val SENDDESPITELOGOUT = "SEND_DESPITE_LOGOUT"

    //todo value to store weither we are synced with server.
    private var syncedWithServer = false

    //user id, stuff for login
    fun userValidation(
        email: String,
        passHash: String,
        callback: (Boolean,String) -> Unit
    ) {
        try {
            validateUserBySharedPreferences(email, passHash,callback)
        } catch (e: NullPointerException) {
            if(isConnected){
                validateUserByServer(email, passHash, callback)
            }else{
                callback(false,"offline device")
            }
        }
    }

    fun getUserId(): String?{
        return preferences.getString("personID", null)
    }

    // TODO: check if it is really necessary to validate by sharedpref as all user data is deleted when user logs out
    private fun validateUserBySharedPreferences(
        email: String,
        passHash: String,
        callback: (Boolean,String) -> Unit
    ) {

        val em = preferences.getString("email", null)
        val ph = preferences.getString("passHash", null)

        if (em!! == email && ph!! == passHash) {
            Log.d("logIn", "SHARED PREFS: SUCCESS. SEND TO NEXT ACTIVITY")
            callback(true,"")
        } else {
            Log.d("logIn", "SHARED PREFS: FAIL. EMAIL/PASSWORD IS INCORRECT")
            callback(false,"email/password is incorrect")
        }
    }

    private fun validateUserByServer(
        email: String,
        passHash: String, callback: (Boolean, String) -> Unit
    ) {

        // url
        val parameters = "em=$email&ph=$passHash"
        val url = "${urlBase}methodPostRemoteLogin?$parameters"
        sendLoginRequest(url, callback)
    }

    private fun sendLoginRequest(url: String, callback: (Boolean, String) -> Unit) {
        // Request queue
        // TODO: Check if we can only have one queue per activity
        //queue = Volley.newRequestQueue(context)

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
                callback(true, "")
                //startActivity(context,Intent(context, TabActivity::class.java), null)
            },
            {

                // TODO: check if due to incorrect password or no contact with server (network/server down)
                Log.d("logIn", "SERVER: FAILED TO CONNECT")
                callback(false, "failed to connect to server")

            })


        queue?.add(jsonRequest)
    }

    private fun insertIntoSharedPreferences(
        email: String,
        passHash: String,
        personID: String
    ) {
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
        //TODO register that if there is still data left, it should be kept and sendt later
        preferences.edit().apply {
            remove("email")
            remove("passHash")
            remove("personID")
            //val numbOfClaims = preferences.getInt("numberOfClaims",0)
            clear()
            apply()
        }
    }

    fun changePassword(password: String, passHash: String, callback: (Boolean,String) -> Unit) {

        if(isConnected){
            callback(false, "offline device")
        }

        //call  then change in shared pref
        val email = preferences.getString("email", null)
        val personID = preferences.getString("personID", null)


        val parameters = "em=$email&np=$password&ph=$passHash"
        val url = "${urlBase}methodPostChangePasswd?$parameters"

        // stringRequest
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { _ ->
                // Updating shared preferences
                insertIntoSharedPreferences(email, passHash, personID)
                callback(true,"")
                Log.d("changePass", "SERVER: SUCCESS. now (email: $email, password:$password and passHash: $passHash)")
            },
            {
                callback(false, "failed to connect ot server")
                Log.d("changePass", "SERVER: FAILED TO CONNECT")
            })

        queue?.add(stringRequest)
        return

    }

    //get number of claims
    fun getNumberOfClaims(): Int{
        return preferences.getInt("numberOfClaims",0)
    }

    //get each claimField by id/get all claimfields for one id
    fun getClaimDataFromSharedPrefrences(id: Int): Claim {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return Claim(
            preferences.getString("claimID$id", ""),
            preferences.getString(
                "claimDes$id",
                ""
            ),
            preferences.getString("claimPhoto$id", ""),
            preferences.getString("claimLocation$id", ""),
            preferences.getString("claimStatus$id", "")
        )
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
            Claim(
                preferences.getString("claimID$i", "na"),
                preferences.getString("claimDes$i", "na"),
                preferences.getString("claimPhoto$i", "na"),
                preferences.getString("claimLocation$i", "na"),
                preferences.getString("claimStatus$i", "na")
            )
        }.toMutableList()
    }

    //takes a call back method that adds the values to shared preference.
    //TODO WHAT IF NEW CLAIM IS MADE OR EDITED BEFORE FETCHED CLAIM FROM SERVER? A status number to compare versions? a conflicting claims activity?
    fun getAllClaimsFromServer(){
        //send the request
        val personId = getUserId()
        val parameters =  "id=$personId"
        val url = "${urlBase}getMethodMyClaims?$parameters"
        if(isConnected){
            sendMyClaimsRequest(url)
            getAllImages()
        } else {
            offlineRequests.add{
                sendMyClaimsRequest(url)
                getAllImages()
            }
        }
    }

    private fun sendMyClaimsRequest(url: String){
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

    private fun getAllImages(){
        //for alle bilder
        for(i in 0..preferences.getInt("numberOfClaims", 0)){
            val photoname = preferences.getString("claimPhoto$i", null)
            if (photoname != null){  //TODO check for existing files:  && noFiles(photoname)){
                val url = "${urlBase}getMethodDownloadPhoto?$photoname"
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        Log.d("GET_IMAGE", "SERVER: SUCCESS. ServerClaims put in sharedpref")
                        //create file
                        createPhotoFile(photoname,response)
                    },
                    {
                        // TODO: check if due to incorrect password or no contact with server (network/server down)
                        Log.d("GET_IMAGE", "SERVER: FAILED DUE TO: ${it.message}")
                    })
                queue?.add(stringRequest)
            }
        }
    }

    /*private fun noFiles(photoName: String): Boolean {
        return true //se etter filer i gallery
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }*/

    private fun createPhotoFile(photoName: String, response: String){
        //lag bildefil og skriv response
        // Create an image file name
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        File.createTempFile(
            photoName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            //TODO les inn i filen
        }
    }

    private fun insertServerClaimsIntoSharedPref(
        serverClaimsResponse: JSONObject
    ){
        // serverResponse: D/GET_CLAIMS: {"id":"0","numberOfClaims":"2","claimId":["0","1","na","na","na"],"claimDes":["desc00","desc01","na","na","na"],"claimPhoto":["phot00","phot01","na","na","na"],"claimLocation":["50-10","50-15","na","na","na"],"claimStatus":["na","na","na","na","na"]}
        val numOfClaims: Int = serverClaimsResponse.getString("numberOfClaims").toInt()
        preferences.edit().apply{
            putInt("numberOfClaims", numOfClaims)
            //read of claims and put into prefrences
            for (i in 0 until numOfClaims) {
                putString("claimID$i", i.toString())
                putString("claimDes$i", serverClaimsResponse.getJSONArray("claimDes")[i].toString())
                putString(
                    "claimPhoto$i",
                    serverClaimsResponse.getJSONArray("claimPhoto")[i].toString()
                )
                putString(
                    "claimLocation$i",
                    serverClaimsResponse.getJSONArray("claimLocation")[i].toString()
                )
            }
            apply()
        }
        Log.d("GET_CLAIMS", "SERVER response put into shared preferences: ${serverClaimsResponse}")
    }


    //add new claim, sharedpref, and try to send to server
    fun addClaim(
        numbOfClaims: Int,
        claim: Claim,
        imageString: String?
    ){
        insertClaimIntoSharedPreferences(numbOfClaims, claim)
        val personID = preferences.getString("personID", "na")
        if(isConnected){
            Log.d("HANDLE_OFFLINE", "make request now!")
            addClaimToServer(claim, personID)
            if (imageString != null){
                addImageToServer(claim, personID, imageString)
            }
        } else {
            Log.d("HANDLE_OFFLINE", "make request later!")
            offlineRequests.add { addClaimToServer(claim, personID) }
            if (imageString != null){
                offlineRequests.add { addImageToServer(claim, personID, imageString) }
            }
        }
    }

    private fun insertClaimIntoSharedPreferences(
        numbOfClaims: Int,
        claim: Claim
    ){
        preferences.edit().apply{
            putInt("numberOfClaims", numbOfClaims + 1)
            putString("claimID$numbOfClaims", numbOfClaims.toString())
            putString("claimDes$numbOfClaims", claim.claimDes)
            putString("claimPhoto$numbOfClaims", claim.claimPhoto)
            putString("claimLocation$numbOfClaims", claim.claimLocation)
            apply()
        }
    }

    private fun addClaimToServer(claim: Claim, personID: String){
        val status = "0"
        //public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
        val parameters = "userId=$personID&indexUpdateClaim=${claim.claimID}&newClaimDes=${claim.claimDes}&newClaimPho=${claim.claimPhoto}&newClaimLoc=${claim.claimLocation}&newClaimSta=$status"
        val url = "http://$ip:$port/postInsertNewClaim?$parameters"
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { _ ->
                //response to successful request
                Log.d("ADD_CLAIM", "SERVER: SUCCESS, added ${claim.toString()}")
            },
            //response to unsuccessful request
            { error ->
                Log.d("ADD_CLAIM", "SERVER: FAILED TO CONNECT WITH $url")
                Log.d("ADD_CLAIM", "SERVER: FAILED DUE TO ${error.message}")
                //handle error if server down, note that not connected and put in que again? maybe some resting time.
                //handle error if network low.
            }
        )
        stringRequest.tag = SENDDESPITELOGOUT
        queue?.add(stringRequest)
    }

    //	public String postMethodUploadPhoto(@RequestParam String userId, @RequestParam String claimId, @RequestParam String fileName, @RequestParam String imageStringBase64) {
    private fun addImageToServer(claim: Claim, personID: String, imageString: String){
        //read it to stringbase64
        queue = Volley.newRequestQueue(context)
        val parameters = "userId=$personID&claimId=${claim.claimID}&fileName=${claim.claimPhoto}&imageStringBase64=${imageString}"
        val url = "http://$ip:$port/postMethodUploadPhoto?$parameters"
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { _ ->
                //response to successful request
                Log.d("ADD_CLAIM", "SERVER: SUCCESS.$imageString")
            },
            //response to unsuccessful request
            { error ->
                Log.d("ADD_CLAIM_IMAGE", "SERVER: FAILED TO CONNECT WITH $url")
                Log.d("ADD_CLAIM_IMAGE", "SERVER: FAILED DUE TO ${error.message}")
                //handle error if server down, note that not connected and put in que again? maybe some resting time.
                //handle error if network low.
            }
        )
        stringRequest.tag = SENDDESPITELOGOUT
        queue?.add(stringRequest)
    }

    fun updateClaim(
        claim: Claim,
        imageString: String
    ){
        val status = updateClaimInSharedPreferences(claim)
        val personID = preferences.getString("personID", "na")
        if(isConnected){
            Log.d("HANDLE_OFFLINE", "make request now!")
            updateClaimInServer(claim, status, personID)
            if (imageString != null){
                addImageToServer(claim, personID, imageString)
            }
        } else {
            Log.d("HANDLE_OFFLINE", "make request later!")
            offlineRequests.add { updateClaimInServer(claim, status, personID) }
            if (imageString != null){
                offlineRequests.add { addImageToServer(claim, personID, imageString) }
            }
        }
    }

    private fun updateClaimInSharedPreferences(claim: Claim): Int{
        val prevStatus = preferences.getString("claimStatus${claim.claimID}", "0").toInt()
        preferences.edit().apply{
            putString("claimDes${claim.claimID}", claim.claimDes)
            putString("claimPhoto${claim.claimID}", claim.claimPhoto)
            putString("claimLocation${claim.claimID}", claim.claimLocation)
            putString("claimStatus${claim.claimID}", (prevStatus + 1).toString())
            apply()
        }
        return prevStatus+1
    }

    private fun updateClaimInServer(claim: Claim, status: Int, personID: String){
        //public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
        val parameters = "userId=$personID&indexUpdateClaim=${claim.claimID}&newClaimDes=${claim.claimDes}&newClaimPho=${claim.claimPhoto}&newClaimLoc=${claim.claimPhoto}&newClaimSta=${status}"
        val url = "http://$ip:$port/postUpdateClaim?$parameters"
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { _ ->
                //response to successful request
                Log.d("UPDATE_CLAIM", "SERVER: SUCCESS.")
            },
            //response to unsuccessful request
            { error ->
                Log.d("UPDATE_CLAIM", "SERVER: FAILED TO CONNECT WITH $url")
                Log.d("UPDATE_CLAIM", "SERVER: FAILED DUE TO ${error.message}")
                //handle error if server down, note that not connected and put in que again? maybe some resting time.
                //handle error if network low.
            }
        )
        queue?.add(stringRequest)
    }

    fun doWaitingRequests(){
        Log.d("HANDLE_OFFLINE", "make offlined requests now")
        while (isConnected && offlineRequests.isNotEmpty()){
            offlineRequests.removeAt(0)()
        }
    }
}