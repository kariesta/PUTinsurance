package com.example.putinsurance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.viewmodels.LoginViewModel
import com.example.putinsurance.R

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

}
