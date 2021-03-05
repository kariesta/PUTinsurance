package com.example.putinsurance

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.RequestQueue
import kotlinx.android.synthetic.main.claim_list_item.view.*


class MainActivity : AppCompatActivity() {
    val MAX_CLAIMS = 5
    private var claimCards: MutableList<ClaimListItem> = mutableListOf()

    private var queue: RequestQueue? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref : SharedPreferences = getSharedPreferences("MYPREF", Context.MODE_PRIVATE)
        fillPref(sharedPref)

        //Hent listen fra shared file eller fra server
        claimCards = getClaimCards(sharedPref)


        val viewManager = LinearLayoutManager(this)
        val viewAdapter: RecyclerView.Adapter<ClaimListAdapter.ViewHolder> = ClaimListAdapter(this, claimCards)
        var recycView = findViewById<RecyclerView>(R.id.recyclerViewClaims).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }


    }


    private class ClaimListAdapter(private val context: Context, private val myDataset: MutableList<ClaimListItem>) :
        RecyclerView.Adapter<ClaimListAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ViewHolder(val cardView: View) : RecyclerView.ViewHolder(cardView)


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ClaimListAdapter.ViewHolder {
            // create a new view
            val cardView = LayoutInflater.from(context)
                .inflate(R.layout.claim_list_item, parent, false) as View
            // set the view's size, margins, paddings and layout parameters

            return ViewHolder(cardView)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.cardView.claimIdField.text = "Id: ${myDataset[position].claimID} "
            holder.cardView.claimLocField.text = "Loc: ${myDataset[position].claimLocation}"
            holder.cardView.claimDesField.text = "${myDataset[position].claimDes}"
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = myDataset.size
    }


    private fun fillPref(sharedPref: SharedPreferences) {
        val editor = sharedPref.edit()
        editor.putInt("personID", 0)
        editor.putInt("numberOfClaims", 2)
        editor.putStringSet("claimIDs", mutableSetOf("0", "1"))
        editor.putStringSet("claimDes", mutableSetOf("desc00", "desc01"))
        editor.putStringSet("claimPhoto", mutableSetOf("photo0", "photo1"))
        editor.putStringSet("claimLocation", mutableSetOf("50-10", "50-15"))
        //editor.putStringSet("claimStatus", mutableSetOf("0", "1"))
        editor.commit()

    }

    private  fun getClaimCards(sharedPref: SharedPreferences): MutableList<ClaimListItem>{
        val numbOfClaims = sharedPref.getInt("numberOfClaims",0)
        val claimIDs = sharedPref.getStringSet("claimIDs", setOf("0"))
        val claimDeses = sharedPref.getStringSet("claimDes",setOf("0"))
        val claimPhotos = sharedPref.getStringSet("claimPhoto",setOf("0"))
        val claimLocations = sharedPref.getStringSet("claimLocation",setOf("0"))
        Log.d("SHAREDPREF","this is now $numbOfClaims, ${claimIDs.first()}, ${claimDeses.first()}, ${claimPhotos.first()}, ${claimLocations.first()}")

        var claims: MutableList<ClaimListItem> = mutableListOf()
        //lag "kort" for hver claim.
        for (i in 0 until numbOfClaims){
            claims.add(ClaimListItem(claimIDs.elementAt(i),claimDeses.elementAt(i),claimPhotos.elementAt(i),claimLocations.elementAt(i)))
            //claimCards[i].visibility = View.VISIBLE
            //claimCards[i].text = claimDes.elementAt(i)
        }
        return claims
    }
}
