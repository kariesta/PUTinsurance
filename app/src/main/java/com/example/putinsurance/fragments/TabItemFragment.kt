package com.example.putinsurance.fragments

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.putinsurance.utils.InjectorUtils
import com.example.putinsurance.R
import com.example.putinsurance.data.Claim
import com.example.putinsurance.viewmodels.TabViewModel
import kotlinx.android.synthetic.main.fragment_tab_item.*


/**
 * A placeholder fragment containing a simple view.
 */
class TabItemFragment : Fragment() {

    private lateinit var tabViewModel : TabViewModel
    private lateinit var claim: Claim
    private var sectionNumber: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val factoryState = InjectorUtils.provideTabItemViewModelFactory(requireContext())
        stateViewModel = ViewModelProvider(this,factoryState).get(StateViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }*/

        Log.d("TabItem", "Creating fragment ${arguments?.getInt(ARG_SECTION_NUMBER) ?: 1}")
        sectionNumber = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1

        // Not sure if good to do...
        val factoryTab = InjectorUtils.provideTabViewModelFactory(requireActivity())
        tabViewModel = ViewModelProvider(this.requireActivity(), factoryTab).get(TabViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tab_item, container, false)

        tabViewModel.getClaim(sectionNumber).observe(viewLifecycleOwner, Observer<Claim> {
            Log.d("TI id", it.claimID)
            Log.d("TI location", it.claimLocation)
            Log.d("TI description", it.claimLocation)

            claim = it
            claimIdField.text = it.claimID
            claimLocField.setText(it.claimLocation)
            claimDesField.setText(it.claimDes)
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editClaimButton.setOnClickListener {

            if (editClaimButton.text.toString() == "Save") {

                claim.claimLocation = claimLocField.text.toString()
                claim.claimDes = claimDesField.text.toString()

                //stateViewModel.updateClaim(claim, "")
                //tabViewModel.notifyChanged(claim) // pretty sure this is bad practice...
                tabViewModel.updateClaim(claim, "") // not sure if using this viewmodel is okay


                setNotEditable(claimLocField)
                setNotEditable(claimDesField)
                editClaimButton.text = "Edit"

            } else {

                setEditable(claimLocField, InputType.TYPE_NUMBER_FLAG_DECIMAL)
                setEditable(claimDesField, InputType.TYPE_CLASS_TEXT)
                editClaimButton.text = "Save"
            }

        }
    }

    private fun setEditable(view: EditText, input: Int) {
        view.apply {
            inputType = input
            isEnabled = true
            setTextColor(ContextCompat.getColor(context, R.color.colorError))
        }
    }

    private fun setNotEditable(view: EditText) {
        view.apply {
            inputType = InputType.TYPE_NULL
            isEnabled = false
            setTextColor(ContextCompat.getColor(context, R.color.colorOnPrimary))
        }
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