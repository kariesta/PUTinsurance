package com.example.putinsurance.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.utils.InjectorUtils
import com.example.putinsurance.R


/**
 * A placeholder fragment containing a simple view.
 */
class TabItemFragment : Fragment() {

    private lateinit var stateViewModel: StateViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = InjectorUtils.provideTabItemViewModelFactory(this.requireContext())
        stateViewModel = ViewModelProvider(this,factory).get(StateViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tab_item, container, false)

        val claimLocView: TextView = root.findViewById(R.id.claimLocField)
        val claimDesView: TextView = root.findViewById(R.id.claimDesField)
        val claimIDView: TextView = root.findViewById(R.id.claimIdField)


        stateViewModel.locText.observe(this.viewLifecycleOwner, Observer<String> {
            claimLocView.text = it
        })
        stateViewModel.descText.observe(this.viewLifecycleOwner, Observer<String> {
            claimDesView.text = it
        })
        stateViewModel.idText.observe(this.viewLifecycleOwner, Observer<String> {
            claimIDView.text = it
        })
        return root
    }




    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): TabItemFragment {
            return TabItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}