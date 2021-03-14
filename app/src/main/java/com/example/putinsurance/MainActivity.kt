package com.example.putinsurance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.claim_list_item.view.*


class MainActivity : AppCompatActivity() {
    val MAX_CLAIMS = 5
    private var claimCards: MutableList<Claim> = mutableListOf()
    private lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)

        /** Clearing and filling with testdata*/
        //sharedPref.edit().clear().commit()
        //Log.d("SHAREDPREF","Clearing! ${sharedPref.getInt("numberOfClaims",-1)}")
        //fillTestingPref(sharedPref)
        //Log.d("SHAREDPREF","Filling with dummy! ${sharedPref.getInt("numberOfClaims",-1)}")

        //Hent listen fra shared file eller fra server
        claimCards = getClaimCards(sharedPref)


        val viewManager = LinearLayoutManager(this)
        val viewAdapter: RecyclerView.Adapter<ClaimListAdapter.ViewHolder> = ClaimListAdapter(this, claimCards)
        findViewById<RecyclerView>(R.id.recyclerViewClaims).apply {
            setHasFixedSize(true)

            // using a linear layout manager
            layoutManager = viewManager

            adapter = viewAdapter
        }

    }

    private class ClaimListAdapter(private val context: Context, private val myDataset: MutableList<Claim>) :
        RecyclerView.Adapter<ClaimListAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ViewHolder(val cardView: View) : RecyclerView.ViewHolder(cardView)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ViewHolder {
            // create a new view
            val cardView = LayoutInflater.from(context)
                .inflate(R.layout.claim_list_item, parent, false) as View
            // set the view's size, margins, paddings and layout parameters

            return ViewHolder(cardView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.cardView.claimIdField.text = "ID: ${this.myDataset[position].claimID}"
            holder.cardView.claimLocField.text = "Loc: ${myDataset[position].claimLocation}"
            holder.cardView.claimDesField.text = myDataset[position].claimDes
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }


    private fun fillTestingPref(sharedPref: SharedPreferences) {
        sharedPref.edit().apply{
            putInt("personID", 0)
            putInt("numberOfClaims", 2)
            putString("claimID0", "0")
            putString("claimID1", "1")
            putString("claimDes0", "desc00")
            putString("claimDes1", "desc01")
            putString("claimPhoto0", "photo0.jpg")
            putString("claimPhoto1", "photo1.jpg")
            putString("claimLocation0", "50-10")
            putString("claimLocation1", "51-15")
            apply()
        }
    }

    private  fun getClaimCards(sharedPref: SharedPreferences): MutableList<Claim>{
        val numbOfClaims = sharedPref.getInt("numberOfClaims",0)
        //Log.d("SHAREDPREF","this is now full of $numbOfClaims claims")

        if (numbOfClaims == 0){
            return mutableListOf()
        }
        //lag "kort" for hver claim.
        return (0 until numbOfClaims).map { i: Int ->
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            Claim(sharedPref.getString("claimID$i",""),sharedPref.getString("claimDes$i",""),sharedPref.getString("claimPhoto$i",""),sharedPref.getString("claimLocation$i",""))
        }.toMutableList()
    }

    fun addingClaim(view: View) {
        val numbOfClaims = sharedPref.getInt("numberOfClaims",0)
        if (numbOfClaims >= MAX_CLAIMS) {
            Toast.makeText(this,"claim limit reached", Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(Intent(this, ClaimFormActivity::class.java))
    }
}
