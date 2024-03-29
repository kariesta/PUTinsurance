package com.example.putinsurance.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.net.InetAddress


class DataRepository private constructor(
    private val context: Context,
    private val preferences: SharedPreferences
) {

    companion object {
        @Volatile private var instance: DataRepository? = null
        const val WIFI = "Wi-Fi"
        const val ANY = "Any"
        var isConnected = false
        var serverDown = false
        private val ip = "10.0.2.2"
        private val port = "8080"
        private val urlBase = "http://$ip:$port/"

        fun getInstance(context: Context, preferences: SharedPreferences) = instance ?: synchronized(
            this
        ) {
            instance?: DataRepository(context, preferences).also { instance = it}
        }
    }

    private var queue : RequestQueue? =  Volley.newRequestQueue(context)
    private var offlineGetRequests: MutableList<() -> Unit> = mutableListOf()
    private var offlinePostRequests: MutableList<String> = mutableListOf()

    private val SENDDESPITELOGOUT = "SEND_DESPITE_LOGOUT"
    private var sendImage = false //when photonames are changed either send to server, or request from server.

    //todo value to store weither we are synced with server.
    private var syncedWithServer = false

    //user id, stuff for login
    fun userValidation(
        email: String,
        passHash: String,
        callback: (Boolean, String) -> Any
    ) {
        try {
            validateUserBySharedPreferences(email, passHash, callback)
        } catch (e: NullPointerException) {
            checkServer()
            if(isConnected){
                validateUserByServer(email, passHash, callback)
            }else{
                callback(false, "offline device")
            }
        }
    }

    fun getUserId(): String?{
        return preferences.getString("personID", null)
    }

    private fun validateUserBySharedPreferences(
        email: String,
        passHash: String,
        callback: (Boolean, String) -> Any
    ) {

        val em = preferences.getString("email", null)
        val ph = preferences.getString("passHash", null)

        if (em!! == email && ph!! == passHash) {
            Log.d("logIn", "SHARED PREFS: SUCCESS. SEND TO NEXT ACTIVITY")
            callback(true, "")
        } else {
            Log.d("logIn", "SHARED PREFS: FAIL. EMAIL/PASSWORD IS INCORRECT")
            callback(false, "email/password is incorrect")
        }
    }

    private fun validateUserByServer(
        email: String,
        passHash: String, callback: (Boolean, String) -> Any
    ) {

        // url
        val parameters = "em=$email&ph=$passHash"
        val url = "${urlBase}methodPostRemoteLogin?$parameters"
        sendLoginRequest(url, callback)
    }

    private fun sendLoginRequest(url: String, callback: (Boolean, String) -> Any) {
        // Request queue
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

                Log.d(
                    "logIn",
                    "SERVER: SUCCESS. SEND TO NEXT ACTIVITY (email: $email and passHash: $passHash)"
                )
                callback(true, "")
                //startActivity(context,Intent(context, TabActivity::class.java), null)
            },
            {
                Log.d("logIn", "SERVER: FAILED TO CONNECT")
                callback(false, "failed to connect to server")
                serverDown = true
                isConnected = false
            })
        /*jsonRequest.retryPolicy =
            DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )*/

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

    //delete all data on logout, except from unsendt updates.
    fun deleteFromSharedPreferences() {
        var oldUpdates = offlinePostRequests
        val pastUpdates = preferences.getStringSet("pastUsersCalls",null)
        if (pastUpdates!=null){
            oldUpdates.addAll(pastUpdates)
        }

        preferences.edit().apply {
            clear()
            putStringSet("pastUsersCalls", oldUpdates.toSet())
            apply()
        }
    }

    fun changePassword(password: String, passHash: String, callback: (Boolean, String) -> Unit) {

        if(!isConnected){
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
                callback(true, "")
                Log.d(
                    "changePass",
                    "SERVER: SUCCESS. now (email: $email, password:$password and passHash: $passHash)"
                )
            },
            {
                callback(false, "failed to connect ot server")
                Log.d("changePass", "SERVER: FAILED TO CONNECT")
                serverDown = true
                isConnected = false
            })
        queue?.add(stringRequest)
    }

    //get number of claims
    fun getNumberOfClaims(): Int {
        return preferences.getInt("numberOfClaims", 0)
    }

    //get each claimField by id/get all claimfields for one id
    fun getClaimDataFromSharedPrefrences(id: Int): MutableLiveData<Claim> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        return MutableLiveData(
            Claim(
                preferences.getString("claimID$id", ""),
                preferences.getString(
                    "claimDes$id",
                    ""
                ),
                preferences.getString("claimPhoto$id", ""),
                preferences.getString("claimLocation$id", "-"),
                preferences.getString("claimStatus$id", "")
            )
        )
    }

    //get all claims
    fun getAllClaimsFromSharedPrefrences(): MutableList<Claim>{
        val numbOfClaims = preferences.getInt("numberOfClaims", 0)
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
    fun getAllClaimsFromServer(essensial: Boolean){
        //send the request
        val personId = getUserId()
        val parameters =  "id=$personId"
        val url = "${urlBase}getMethodMyClaims?$parameters"
        if(isConnected){
            sendMyClaimsRequest(url)
        } else if (essensial) {
            offlineGetRequests.add{
                Log.d("OFFLINEACT", "NOW getAllClaimsFromServer $url")
                sendMyClaimsRequest(url)
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
                Log.d("GET_CLAIMS", "SERVER: FAILED DUE TO: ${it.message}")
                serverDown = true
                isConnected = false
                //not adding to offlinerequests to avoid many similar requests
            })
        jsonRequest.retryPolicy =
            DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

        queue?.add(jsonRequest)
    }

    fun updateImage(claimId: Int){
        if(sendImage){
            val photoname = preferences.getString("claimPhoto$claimId", null)
            val pId = getUserId()
            val imageString = preferences.getString(photoname, null)
            val claimToUpdate = getClaimDataFromSharedPrefrences(claimId)
            if (pId!=null && imageString!=null){
                val parameters = "userId=$pId&claimId=${claimToUpdate.value?.claimID}&fileName=${claimToUpdate.value?.claimPhoto}&imageStringBase64=${imageString}"
                val url = "http://$ip:$port/postMethodUploadPhoto?$parameters"
                preferences.edit().apply { putString("updateClaim${claimId}Photo", url); commit()}
                if (isConnected) {
                    Log.d(
                        "UPDATE_IMAGE",
                        "now adding image for $claimId with strings ${imageString.length}"
                    )
                    addImageToServer(url)
                } else {
                    Log.d(
                        "UPDATE_IMAGE",
                        "LATER adding image for $claimId with strings ${imageString.length}"
                    )
                    offlinePostRequests.add(url)/* {
                        Log.d("OFFLINEACT", "NOW addImageToServer$pId")
                        addImageToServer(url)
                    }*/
                }
            }
            else{
                Log.d("UPDATE_IMAGE", "not done because of pid:$pId or imageString:$imageString")
            }
        } else {
            if (isConnected) {
                Log.d("UPDATE_IMAGE", "now fetching  image for $claimId from server")
                getClaimImageFromServer(claimId)
            } else {
                Log.d("UPDATE_IMAGE", "LATER fetching  image for $claimId from server")
                offlineGetRequests.add {
                    Log.d("OFFLINEACT", "NOW getClaimImageFromServer$claimId")
                    getClaimImageFromServer(claimId)
                }
            }
        }
    }

    fun getClaimImageFromServer(claimId: Int) {
        val photoname = preferences.getString("claimPhoto$claimId", null)
        if (photoname != null) {
            val url = "${urlBase}getMethodDownloadPhoto?fileName=$photoname"
            if (isConnected) {
                sendGetImageRequest(url, photoname)
            } else {
                offlineGetRequests.add {
                    Log.d("OFFLINEACT", "NOW sendGetImageRequest $url  $photoname")
                    sendGetImageRequest(url, photoname)
                }
            }
        }
    }

    private fun sendGetImageRequest(url: String, photoname: String) {
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d("GET_IMAGE", "SERVER: SUCCESS. Server image put in sharedpref")
                preferences.edit().apply { putString("$photoname", response);commit() }
                Log.d(
                    "GET_IMAGE", "image $photoname now: ${
                        if (response.length > 10) response.substring(
                            0,
                            10
                        ) else "null"
                    }, see: ${preferences.getString(photoname, null)}"
                )

            },
            {
                Log.d("GET_IMAGE", "SERVER: FAILED DUE TO: ${it.message}")
                serverDown = true
                isConnected = false
                offlineGetRequests.add {
                    Log.d("OFFLINEACT", "NOW sendGetImageRequest $url  $photoname")
                    sendGetImageRequest(url, photoname)
                }
            })
        stringRequest.retryPolicy =
            DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue?.add(stringRequest)
    }

    fun getClaimImageFromPreferences(claimId: Int): Bitmap?{
        //if no photoname return null
        val photoname = preferences.getString("claimPhoto$claimId", null)
        // ?: return Log.d("GET_IMAGE_FAIL","no image for claimPhoto$claimId")
        if(photoname==null){
            Log.d("GET_IMAGE_FAIL", "no image for claimPhoto$claimId")
            return null
        }
        Log.d("Get_Image", "photoname in sharedPref $photoname")
        //find the file and return as a bitmap
        /*val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photopath = storageDir.absolutePath+"/"+photoname
        return BitmapFactory.decodeFile(photopath)*/
        val photoString = preferences.getString(photoname, null)//?: return null
        if(photoString==null){
            Log.d("GET_IMAGE_FAIL", "no image for $photoname")
            return null
        }
        val decodedString: ByteArray = Base64.decode(photoString, Base64.URL_SAFE)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)//Bitmap.Config.ARGB_8888)
        //return BitmapFactory.decodeByteArray(photoString.toByteArray(),0,photoString.length)
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
                sendImage = false
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
            Log.d("GETCLAMS", "photo now ${preferences.getString("claimPhoto0", null)}")
            Log.d("GETCLAMS", "photo now ${preferences.getString("claimPhoto1", null)}")
            Log.d("GETCLAMS", "photo now ${preferences.getString("claimPhoto2", null)}")
        }
        Log.d("GET_CLAIMS", "SERVER response put into shared preferences: ${serverClaimsResponse}")
    }


    //add new claim, sharedpref, and try to send to server
    fun addClaim(
        numbOfClaims: Int,
        claim: Claim,
        imageString: String?
    ){
        Log.d(
            "ADD_CLAIM_ERROR?",
            "Now putting in said imstring for numclam$numbOfClaims clam$claim with imString${
                if (imageString != null && imageString.length > 6) imageString.substring(
                    0,
                    5
                ) else "nothing"
            }"
        )
        preferences.edit().apply(){ putString(claim.claimPhoto, imageString);commit()}
        insertClaimIntoSharedPreferences(numbOfClaims, claim)
        val personID = preferences.getString("personID", "na")
        val parameters = "userId=$personID&indexUpdateClaim=${claim.claimID}&newClaimDes=${claim.claimDes}&newClaimPho=${claim.claimPhoto}&newClaimLoc=${claim.claimLocation}&newClaimSta=0"
        val url = "http://$ip:$port/postInsertNewClaim?$parameters"
        preferences.edit().apply { putString("addClaim${claim.claimID}Content", url); commit()}
        if(isConnected){
            Log.d("HANDLE_OFFLINE", "make request now!")
            addClaimToServer(url)
        } else {
            Log.d("HANDLE_OFFLINE", "make request later!")
            offlinePostRequests.add(url) /*{
                Log.d("OFFLINEACT", "NOW addClaimToServer $claim  $personID")
                addClaimToServer(url)
            }*/
        }
    }

    private fun insertClaimIntoSharedPreferences(
        numbOfClaims: Int,
        claim: Claim
    ){
        Log.d("ADD_CLAIM_ERROR?", "Now putting numclam$numbOfClaims clam$claim in sharedPref")
        preferences.edit().apply{
            putInt("numberOfClaims", numbOfClaims + 1)
            putString("claimID$numbOfClaims", numbOfClaims.toString())
            putString("claimDes$numbOfClaims", claim.claimDes)
            sendImage = true
            putString("claimPhoto$numbOfClaims", claim.claimPhoto)
            putString("claimLocation$numbOfClaims", claim.claimLocation)
            commit()
        }
        Log.d("ADD_CLAIM_ERROR?", "End putting to shared pref")

    }

    private fun addClaimToServer(url: String){
        val status = "0"
        //public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
        //val parameters = "userId=$personID&indexUpdateClaim=${claim.claimID}&newClaimDes=${claim.claimDes}&newClaimPho=${claim.claimPhoto}&newClaimLoc=${claim.claimLocation}&newClaimSta=$status"
        //val url = "http://$ip:$port/postInsertNewClaim?$parameters"
        Log.d("ADD_CLAIM_ERROR?", "sending to server with $url")
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { _ ->
                //response to successful request
                Log.d("ADD_CLAIM", "SERVER: SUCCESSF added a claim}")
            },
            //response to unsuccessful request
            { error ->
                Log.d("ADD_CLAIM", "SERVER: FAILED TO CONNECT WITH $url")
                Log.d("ADD_CLAIM", "SERVER: FAILED DUE TO ${error.message}")
                //handle error if server down, note that not connected and put in que again? maybe some resting time.
                //handle error if network low.
                serverDown = true
                isConnected = false
                offlinePostRequests.add(url)
            }
        )
        stringRequest.retryPolicy =
            DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        stringRequest.tag = SENDDESPITELOGOUT
        queue?.add(stringRequest)
    }

    //	public String postMethodUploadPhoto(@RequestParam String userId, @RequestParam String claimId, @RequestParam String fileName, @RequestParam String imageStringBase64) {
    private fun addImageToServer(url: String){//claim: Claim, personID: String, imageString: String){
        //read it to stringbase64
        //queue = Volley.newRequestQueue(context)
        //val parameters = "userId=$personID&claimId=${claim.claimID}&fileName=${claim.claimPhoto}&imageStringBase64=${imageString}"
        //val url = "http://$ip:$port/postMethodUploadPhoto?$parameters"
        Log.d("UPDATE_IMAGE??", "now sending to $url")
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { _ ->
                preferences.edit().apply {
                    putString(
                        "updateClaim${
                            url.split("=")[2].substring(
                                0,
                                1
                            )
                        }Photo", url
                    ); commit()
                }
                //response to successful request
                Log.d("ADD_CLAIM", "SERVER: SUCCESS.")
            },
            //response to unsuccessful request
            { error ->
                preferences.edit().apply {
                    putString(
                        "updateClaim${
                            url.split("=")[2].substring(
                                0,
                                1
                            )
                        }Photo", url
                    ); commit()
                }
                Log.d("ADD_CLAIM_IMAGE", "SERVER: FAILED TO CONNECT WITH $url")
                Log.d("ADD_CLAIM_IMAGE", "SERVER: FAILED DUE TO ${error.message}")
                //preferences.edit().apply { putString("updateClaim${claimId}Photo",url); commit()} //???
                //handle error if server down, note that not connected and put in que again? maybe some resting time.
                //handle error if network low.
                serverDown = true
                isConnected = false
                offlinePostRequests.add(url)
            }
        )
        stringRequest.retryPolicy =
            DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
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
        val parameters = "userId=$personID&indexUpdateClaim=${claim.claimID}&updateClaimDes=${claim.claimDes}&updateClaimPho=${claim.claimPhoto}&updateClaimLoc=${claim.claimLocation}&updateClaimSta=${status}"
        val url = "http://$ip:$port/postUpdateClaim?$parameters"
        preferences.edit().apply { putString("updateClaim${claim.claimID}", url); commit()}
        if(isConnected){
            Log.d("HANDLE_OFFLINE", "make request now!")
            updateClaimInServer(url)
        } else {
            Log.d("HANDLE_OFFLINE", "make request later!")
            offlinePostRequests.add(url) /* {
                Log.d("OFFLINEACT", "NOW updateClaimInServer $claim $status $personID")
                updateClaimInServer(url)
            }*/
        }
    }

    private fun updateClaimInSharedPreferences(claim: Claim): Int{
        val prevStatus = preferences.getString("claimStatus${claim.claimID}", "0").toInt()
        preferences.edit().apply{
            putString("claimDes${claim.claimID}", claim.claimDes)
            sendImage = true
            putString("claimPhoto${claim.claimID}", claim.claimPhoto)
            putString("claimLocation${claim.claimID}", claim.claimLocation)
            putString("claimStatus${claim.claimID}", (prevStatus + 1).toString())
            apply()
        }
        return prevStatus+1
    }

    private fun updateClaimInServer(url: String){
        //public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
        //val parameters = "userId=$personID&indexUpdateClaim=${claim.claimID}&newClaimDes=${claim.claimDes}&newClaimPho=${claim.claimPhoto}&newClaimLoc=${claim.claimPhoto}&newClaimSta=${status}"
        //val url = "http://$ip:$port/postUpdateClaim?$parameters"
        val stringRequest = StringRequest(
            Request.Method.POST, url,
            { _ ->
                //response to successful request
                preferences.edit().apply { remove("updateClaim${
                    url.split("=")[2].substring(
                        0,
                        1
                    )
                }"); commit() }
                Log.d("UPDATE_CLAIM", "SERVER: SUCCESS.")
            },
            //response to unsuccessful request
            { error ->
                preferences.edit().apply { putString("updateClaim${
                    url.split("=")[2].substring(
                        0,
                        1
                    )
                }", url); commit() }
                Log.d("UPDATE_CLAIM", "SERVER: FAILED TO CONNECT WITH $url")
                Log.d("UPDATE_CLAIM", "SERVER: FAILED DUE TO ${error.message}")
                //handle error if server down, note that not connected and put in que again? maybe some resting time.
                //handle error if network low.
            }
        )
        stringRequest.retryPolicy =
            DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue?.add(stringRequest)
    }

    fun doWaitingRequests(){
        Log.d(
            "HANDLE_OFFLINE",
            "make offlined requests now, there are ${offlinePostRequests.size} "
        )
        Log.d("HANDLE_OFFLINE", "${offlinePostRequests.toString()} ")
        while (isConnected && offlinePostRequests.isNotEmpty()){
            // stringRequest
            val stringRequest = StringRequest(
                Request.Method.POST, offlinePostRequests[0],
                { _ ->
                    offlinePostRequests.removeAt(0)
                },
                {
                    Log.d("changePass", "SERVER: FAILED TO CONNECT")
                    isConnected = false
                    serverDown = true
                })

            queue?.add(stringRequest)
        }
        Log.d("HANDLE_OFFLINE", "make offlined requests now, there are ${offlineGetRequests.size} ")
        Log.d("HANDLE_OFFLINE", "${offlineGetRequests.toString()} ")
        while (isConnected && offlineGetRequests.isNotEmpty()){
            offlineGetRequests.removeAt(0)()
        }
    }

    fun checkServer(){
        return try {
            InetAddress.getByName(urlBase).isReachable(3000) //Replace with your name
            serverDown = false
        } catch (e: Exception) {
            Log.d("CHECK_SERVER","${e.message}")
            serverDown = true
        }
    }

    fun checkForOldUpdates(){
        //get old postrequest URLs from shared preferences and make requests
        var oldUpdates = preferences.getStringSet("pastUsersCalls", null)
        if(oldUpdates==null || oldUpdates.isEmpty()){
            Log.d("CHECK_OLD_UPDATES","no old ones")
            return
        }
        if(!isConnected){
            Log.d("CHECK_OLD_UPDATES","putting olds in offlinePost there are ${oldUpdates.size}")
            offlinePostRequests.addAll(0,oldUpdates)
        }
        while (isConnected && oldUpdates.isNotEmpty()){
            // stringRequest
            val stringRequest = StringRequest(
                Request.Method.POST, oldUpdates.first(),
                { _ ->
                    Log.d("CHECK_OLD_UPDATES","successfully sendt old request")
                    oldUpdates.remove(oldUpdates.first())
                },
                {
                    Log.d("CHECK_OLD_UPDATES","error in sending old request, putting in offlineReqs")
                    isConnected = false
                    serverDown = true
                    offlinePostRequests.addAll(0,oldUpdates)
                    oldUpdates.remove(oldUpdates.first())
                })
            queue?.add(stringRequest)
        }
    }
}