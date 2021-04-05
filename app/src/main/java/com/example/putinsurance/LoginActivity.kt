package com.example.putinsurance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.math.BigInteger
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private val ip = "10.0.2.2"
    private val port = "8080"

    // TODO: try to enable databinding and send these in from xml
    lateinit var email : EditText
    lateinit var password : EditText

    // private val TAG = "LOGIN"
    private var queue : RequestQueue? = null

    // shared preferences
    private lateinit var preferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)

        preferences = this.getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)
    }


    override fun onStop() {
        super.onStop()
        // Cancelling the requests
        //queue?.cancelAll(TAG)
        // I think this is enough for one activity (no tags)
        queue?.cancelAll(this)
    }


    // TODO: check if SINGLETON of shared preferences and queue is recommended
    fun logIn(view: View) {
        // Shared Preferences

        val emailText =  email.text.toString()
        val passwordHash = passwordToHashMD5(password.text.toString())


        // If email and password are in shared pref, nullpointerexception is not thrown
        try {
            validateUserBySharedPreferences(emailText, passwordHash)
        } catch (e : NullPointerException) {
            validateUserByServer(emailText, passwordHash)
        }

    }

    // TODO: delete the rest of the saved data. NB: Check first that all have been pushed to server!
    fun logOut(view: View) {
        deleteFromSharedPreferences()
        Log.d("logIn", "LOGGING OUT")
    }



    // TODO: check if it is really necessary to validate by sharedpref as all user data is deleted when user logs out
    private fun validateUserBySharedPreferences(email: String, passHash: String) {

        val em = preferences.getString("email", null)
        val ph = preferences.getString("passHash", null)

        if (em!! == email && ph!! == passHash) {
            // TODO: Send to next activity
            Log.d("logIn", "SHARED PREFS: SUCCESS. SEND TO NEXT ACTIVITY")
            startActivity(Intent(this, TabActivity::class.java))
        } else {
            // TODO: Show to user that password is incorrect
            Log.d("logIn", "SHARED PREFS: FAIL. EMAIL/PASSWORD IS INCORRECT")
        }
    }

    private fun validateUserByServer(email : String, passHash : String) {

        // url
        val parameters = "em=$email&ph=$passHash"
        val url = "http://$ip:$port/methodPostRemoteLogin?$parameters"

        sendPostRequest(url)

    }

    private fun sendPostRequest(url : String) {
        // Request queue
        // TODO: Check if we can only have one queue per activity
        queue = Volley.newRequestQueue(this)

        // jsonRequest
        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, null,
            { response ->

                // Parsing JSON object
                val email = response.getString("email")
                val passHash = response.getString("passHash")

                // Updating shared preferences
                insertIntoSharedPreferences(email, passHash)

                // TODO: go to next activity
                Log.d("logIn", "SERVER: SUCCESS. SEND TO NEXT ACTIVITY (email: $email and passHash: $passHash)")

            },
            {

                // TODO: check if due to incorrect password or no contact with server (network/server down)
                Log.d("logIn", "SERVER: FAILED TO CONNECT")
            })


        queue?.add(jsonRequest)

    }

    private fun insertIntoSharedPreferences(email : String, passHash: String) {
        preferences.edit().apply {
            putString("email", email)
            putString("passHash", passHash)
            // According to stack overflow, apply is faster than commit as it is asynchronous: https://stackoverflow.com/questions/5960678/whats-the-difference-between-commit-and-apply-in-sharedpreferences
            apply()
        }

    }

    private fun deleteFromSharedPreferences() {
        preferences.edit().apply {
            remove("email")
            remove("passHash")
            apply()
        }
    }

    // Convert to hash using MD5
    // https://www.geeksforgeeks.org/md5-hash-in-java/ USE MD5
    private fun passwordToHashMD5(password : String) : String {
        val bytes = MessageDigest
            .getInstance("MD5")
            .digest(password.toByteArray())

        return BigInteger(1, bytes).toString(16).padStart(32, '0')
    }
}
