package com.example.putinsurance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun next(view: View){
        Navigation.findNavController(view).navigate(R.id.action_blankFragment_to_blankFragment2)
    }
}