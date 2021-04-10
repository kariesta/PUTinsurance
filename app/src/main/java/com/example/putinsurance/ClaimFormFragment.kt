package com.example.putinsurance

import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.putinsurance.ui.main.PlaceholderFragment
import com.example.putinsurance.ui.main.StateViewModel

class ClaimFormFragment : Fragment() {

    companion object {
        fun newInstance() = ClaimFormFragment()
    }

    private lateinit var viewModel: ClaimFormViewModel
    private lateinit var claimFormViewModel: StateViewModel

    private lateinit var sharedPref: SharedPreferences
    private lateinit var imageView: ImageView

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

        viewModel.lat.observe(this, Observer<String> {
            claimLatView.text = it
        })

        viewModel.long.observe(this, Observer<String> {
            claimLongView.text = it
        })

        viewModel.desc.observe(this, Observer<String> {
            claimDescView.text = it
        })

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}