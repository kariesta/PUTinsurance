package com.example.putinsurance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class ClaimFormActivity : AppCompatActivity() {

    private val ip = "10.0.2.2"
    private val port = "8080"
    private lateinit var sharedPref: SharedPreferences
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claim_form)
        sharedPref = getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)
    }


    //TODO: put location in form
    //TODO: take photo

    fun submitClaim(view: View) {
        Log.d("ADD_CLAIM","this claim add has started")

        //collect all data from form
        val photoName = "aPhoto"
        val longString = findViewById<TextView>(R.id.LongitudeField).text.toString()
        val latString = findViewById<TextView>(R.id.LatitudeField).text.toString()
        val descString = findViewById<TextView>(R.id.DescriptionField).text.toString()
        val numbOfClaims = sharedPref.getInt("numberOfClaims",0)


        //Legger inn nye verdier
        insertClaimIntoSharedPreferences(numbOfClaims,descString,longString,latString,photoName)
        sendClaimToServer(numbOfClaims,descString,longString,latString,photoName)
        Toast.makeText(this,"New claim added",Toast.LENGTH_SHORT).show()
        Log.d("ADD_CLAIM","this will now be updated asynchronously with: ${sharedPref.getInt("numberOfClaims", 0)}, ${ sharedPref.getString("claimID$numbOfClaims","Null")}, ${ sharedPref.getString("claimDes$numbOfClaims","")}, ${ sharedPref.getString("claimPhoto$numbOfClaims","")}, ${ sharedPref.getString("claimLocation$numbOfClaims","")}")
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun insertClaimIntoSharedPreferences(numbOfClaims: Int, descString: String, longString: String, latString: String, photoName: String){
        sharedPref.edit().apply{
            putInt("numberOfClaims", numbOfClaims+1)
            putString("claimID$numbOfClaims",numbOfClaims.toString())
            putString("claimDes$numbOfClaims",descString)
            putString("claimPhoto$numbOfClaims","$photoName${numbOfClaims}.jpg")
            putString("claimLocation$numbOfClaims","$longString-$latString")
            apply()
        }
    }

    private fun sendClaimToServer(claimID: Int, descString: String, longString: String, latString: String, photoName: String){
        /*val userId = 0
        val status = "na"
        //public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
        queue = Volley.newRequestQueue(this)
        val parameters = "userId=$userId&indexUpdateClaim=$claimID&newClaimDes=$descString&newClaimPho=$photoName&newClaimLoc=$longString-$latString&newClaimSta=$status"
        val url = "http://$ip:$port/postInsertNewClaim?$parameters"
        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, null,
            {
                Log.d("ADD_CLAIM", "SERVER: SUCCESS.$it")
            },
            {
                Log.d("ADD_CLAIM", "SERVER: FAILED TO CONNECT WITH $url")
                //TODO: try again later
            }
        )
        queue.add(jsonRequest)*/

    }

}