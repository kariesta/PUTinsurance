package com.example.putinsurance

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import com.example.putinsurance.data.DataRepository
import com.example.putinsurance.data.DataRepository.Companion.ANY
import com.example.putinsurance.data.DataRepository.Companion.WIFI
import com.example.putinsurance.data.DataRepository.Companion.isConnected
import com.example.putinsurance.data.DataRepository.Companion.serverDown
import com.google.android.material.snackbar.Snackbar

class NetworkReceiver constructor(private val dataRepository: DataRepository) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val conn = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = conn.activeNetworkInfo
        val netcap: NetworkCapabilities? = if (conn.activeNetwork!=null) conn.getNetworkCapabilities(
            conn.activeNetwork
        ) else null

        //sharedPref = getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)

        // The user's current network preference setting.
        val sPref: String = if (context.getSharedPreferences(
                "com.example.putinsurance_preferences",
                Context.MODE_PRIVATE
            ).getBoolean("only_wi-fi_sync", true)) "Wi-Fi" else "Any"

        // Checks the user prefs and the network connection. Based on the result, decides whether
        // to refresh the display or keep the current display.
        // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
        // otherwise check for any(mobile) network
        if((netcap != null && WIFI == sPref && netcap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))|| (netcap != null && ANY == sPref)){
            if (serverDown){
                //test ut og sett til false hvis det funker.
                dataRepository.checkServer()
            }
            if (!serverDown){
                Log.d("networked", "connected!")
                Toast.makeText(context, "connected!", Toast.LENGTH_SHORT).show()
                isConnected = true
                dataRepository.doWaitingRequests()
            } else {
                Log.d("networked", "server down!")
            }
        } else {
            isConnected  = false
            Log.d(
                "networked", "Action: ${intent.action}, prefs: ${WIFI} == ${sPref}, wif: ${
                    netcap?.hasTransport(
                        NetworkCapabilities.TRANSPORT_WIFI
                    )
                }, net: ${networkInfo.toString()} "
            )
        }
    }
}