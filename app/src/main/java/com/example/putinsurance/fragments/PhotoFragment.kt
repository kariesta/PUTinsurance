package com.example.putinsurance.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.putinsurance.R
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
        // is it because i detach twice?
        tabViewModel.index.observe(viewLifecycleOwner, Observer<Int> {
            Log.d("Switch", "setting new image resource")
            when (it) {
                0 -> photo_accident.setImageResource(R.drawable.car_crash_0)
                1 -> photo_accident.setImageResource(R.drawable.ic_baseline_add_24)
                2 -> photo_accident.setImageResource(R.drawable.ic_launcher_foreground)
                3 -> photo_accident.setImageResource(R.drawable.car_crash_0)
                4 -> photo_accident.setImageResource(R.drawable.car_crash_0)
            }
        })
    }

}