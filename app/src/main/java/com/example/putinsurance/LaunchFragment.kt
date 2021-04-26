package com.example.putinsurance

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.putinsurance.data.DataRepository
import com.example.putinsurance.utils.InjectorUtils

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class LaunchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataRepository = InjectorUtils.getDataRepository(this.requireContext())

        //launchview does not have time to appear, but not really needed. We mainly want the conditional navigation to next fragment.
        val nav = findNavController()
        if(dataRepository.getUserId()==null){
            nav.navigate(R.id.action_fullscreenFragment_to_loginFragment)
        } else {
            nav.navigate(R.id.action_fullscreenFragment_to_tabFragment)
        }
    }
}