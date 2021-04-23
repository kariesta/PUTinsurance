package com.example.putinsurance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.putinsurance.data.DataRepository
import java.math.BigInteger
import java.security.MessageDigest

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java).apply {
            //set up any value here, like setLocation()
        }
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        val emailField: TextView = root.findViewById(R.id.editTextTextEmailAddress)
        val passwordField: TextView = root.findViewById(R.id.editTextTextPassword)
        viewModel.email.observe(this.viewLifecycleOwner, Observer<String> {
            emailField.text = it
        })
        viewModel.password.observe(this.viewLifecycleOwner, Observer<String> {
            passwordField.text = it
        })
        return root
    }




    /*// TODO: check if it is really necessary to validate by sharedpref as all user data is deleted when user logs out
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
    */


}
