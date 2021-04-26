package com.example.putinsurance.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.putinsurance.R
import com.example.putinsurance.viewmodels.ClaimFormViewModel

class ClaimFormFragment : Fragment() {

    companion object {
        fun newInstance() = ClaimFormFragment()
    }

    private lateinit var viewModel: ClaimFormViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ClaimFormViewModel::class.java).apply {
            //set up any value here, like setLocation()
        }
        val root = inflater.inflate(R.layout.claim_form_fragment, container, false)
        val claimPhotoPreviewView: ImageView = root.findViewById(R.id.photoPreviewView)
        val claimLatView: TextView = root.findViewById(R.id.LatitudeField)
        val claimLongView: TextView = root.findViewById(R.id.LongitudeField)
        val claimDescView: TextView = root.findViewById(R.id.DescriptionField)

        viewModel.lat.observe(this.viewLifecycleOwner , Observer<String> {
            claimLatView.text = it
        })

        viewModel.long.observe(this.viewLifecycleOwner, Observer<String> {
            claimLongView.text = it
        })

        viewModel.desc.observe(this.viewLifecycleOwner, Observer<String> {
            claimDescView.text = it
        })

        return root
    }

}