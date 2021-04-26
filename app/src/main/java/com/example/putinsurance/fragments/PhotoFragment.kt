package com.example.putinsurance.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.putinsurance.R
import com.example.putinsurance.data.Claim
import com.example.putinsurance.utils.InjectorUtils
import com.example.putinsurance.viewmodels.TabViewModel
import kotlinx.android.synthetic.main.photo_fragment.*

class PhotoFragment : Fragment() {

    companion object {
        fun newInstance() = PhotoFragment()
    }

    private lateinit var tabViewModel: TabViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.photo_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = InjectorUtils.provideTabViewModelFactory(requireActivity())
        tabViewModel = ViewModelProvider(requireActivity(), factory).get(TabViewModel::class.java)

        // I don't understand why this is called when we go back, even when map is showed?
        // is it because i detach twice? Not detached twice anymore. Still called.

        // When just photo is observed, it doesn't trigger when there is no photo (as the string remains 0)
        // Maybe it's better to observe only photo?
        tabViewModel.claim.observe(viewLifecycleOwner, Observer<Claim> {
            Log.d("Fetch", "Setting new image resource")

            // TODO
            photo_accident.setImageBitmap(it.claimPhoto.toBitmap())

            /*when (it) {
                0 -> photo_accident.setImageResource(R.drawable.car_crash_0)
                1 -> photo_accident.setImageResource(R.drawable.ic_baseline_add_24)
                2 -> photo_accident.setImageResource(R.drawable.ic_launcher_foreground)
                3 -> photo_accident.setImageResource(R.drawable.car_crash_0)
                4 -> photo_accident.setImageResource(R.drawable.car_crash_0)
            }*/
        })
    }

    // From here: https://www.javaer101.com/en/article/16912872.html
    // Wrote it as an extension function
    // Might place it in it's own file.
    private fun String.toBitmap() : Bitmap? {
        val imageBytes = Base64.decode(this, 0)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

}