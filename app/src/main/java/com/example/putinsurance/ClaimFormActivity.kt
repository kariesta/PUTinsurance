package com.example.putinsurance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

class ClaimFormActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences

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
        sendClaimToServer()
        Toast.makeText(this,"New claim added",Toast.LENGTH_SHORT).show()
        Log.d("ADD_CLAIM","this will now be updated asynchronously with: ${sharedPref.getInt("numberOfClaims", 0)}, ${ sharedPref.getString("claimID$numbOfClaims","Null")}, ${ sharedPref.getString("claimDes$numbOfClaims","")}, ${ sharedPref.getString("claimPhoto$numbOfClaims","")}, ${ sharedPref.getString("claimLocation$numbOfClaims","")}")
        sendClaimToServer()
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

    private fun sendClaimToServer(){
        //TODO: send new claim to server
    }

}