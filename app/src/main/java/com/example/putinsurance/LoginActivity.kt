package com.example.putinsurance

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.math.BigInteger
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private val ip = "10.0.2.2"
    private val port = "8080"

    lateinit var email : EditText
    lateinit var password : EditText

    // private val TAG = "LOGIN"
    private var queue : RequestQueue? = null

    // shared preferences
    lateinit var preferences : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)

        preferences = this.getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)
    }

    // TODO: check if SINGLETON of
    // TODO: How to get password hash?
    // TODO: How to send parameters for post method
    fun logIn(view: View) {
        // Shared Preferences

        val emailText =  email.text.toString()
        val passwordHash = passwordToHashMD5(password.text.toString())

        validateUserByServer(emailText, passwordHash)







    }

    fun validateUserByServer(email : String, passwordHash : String) {

        // url
        val parameters = "em=$email&ph=$passwordHash"
        val url = "http://$ip:$port/methodPostRemoteLogin?$parameters"

        sendPostRequest(url)

    }

    private fun sendPostRequest(url : String) {
        // Request queue
        queue = Volley.newRequestQueue(this) // TODO: Check if we can only have one queue per activity

        // stringrequest
        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, null,
            { response ->
                // TODO: Send to next activity
                Log.d("logIn", "response $response")
            },
            { Log.d("logIn", "FAILED TO CONNECT") })

        // To be able to cancel requests using this tag
        //stringRequest.tag = TAG

        queue?.add(jsonRequest)
    }

    fun validateBySharedPreferences() {

    }

    override fun onStop() {
        super.onStop()
        // Cancelling the requests
        //queue?.cancelAll(TAG)
        // I think this is enough for one activity (no tags)
        queue?.cancelAll(this)
    }

    // Convert to hash somehow
    // https://www.geeksforgeeks.org/md5-hash-in-java/ USE MD5
    private fun passwordToHashMD5(password : String) : String {
        val bytes = MessageDigest
            .getInstance("MD5")
            .digest(password.toByteArray())

        return BigInteger(1, bytes).toString(16).padStart(32, '0')
    }
}
