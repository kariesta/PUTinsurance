package com.example.putinsurance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.android.volley.RequestQueue
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ClaimFormActivity : AppCompatActivity() {

    private val ip = "10.0.2.2"
    private val port = "8080"
    private lateinit var sharedPref: SharedPreferences
    private lateinit var queue: RequestQueue
    private lateinit var imageView: ImageView
    private lateinit var imageBitmap: Bitmap

    lateinit var currentPhotoPath: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claim_form)
        sharedPref = getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)
        imageView = findViewById<ImageView>(R.id.imageView)
    }


    fun submitClaim(view: View) {
        Log.d("ADD_CLAIM", "this claim add has started")

        //collect all data from form
        val photoName = currentPhotoPath
        val longString = findViewById<TextView>(R.id.LongitudeField).text.toString()
        val latString = findViewById<TextView>(R.id.LatitudeField).text.toString()
        val descString = findViewById<TextView>(R.id.DescriptionField).text.toString()
        val numbOfClaims = sharedPref.getInt("numberOfClaims", 0)


        //Legger inn nye verdier
        insertClaimIntoSharedPreferences(numbOfClaims, descString, longString, latString, photoName)
        sendClaimToServer(numbOfClaims, descString, longString, latString, photoName)
        Toast.makeText(this, "New claim added", Toast.LENGTH_SHORT).show()
        Log.d(
            "ADD_CLAIM", "this will now be updated asynchronously with: ${
                sharedPref.getInt(
                    "numberOfClaims",
                    0
                )
            }, ${sharedPref.getString("claimID$numbOfClaims", "Null")}, ${
                sharedPref.getString(
                    "claimDes$numbOfClaims",
                    ""
                )
            }, ${sharedPref.getString("claimPhoto$numbOfClaims", "")}, ${
                sharedPref.getString(
                    "claimLocation$numbOfClaims",
                    ""
                )
            }"
        )
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun insertClaimIntoSharedPreferences(
        numbOfClaims: Int,
        descString: String,
        longString: String,
        latString: String,
        photoName: String
    ){
        sharedPref.edit().apply{
            putInt("numberOfClaims", numbOfClaims + 1)
            putString("claimID$numbOfClaims", numbOfClaims.toString())
            putString("claimDes$numbOfClaims", descString)
            putString("claimPhoto$numbOfClaims", "$photoName${numbOfClaims}.jpg")
            putString("claimLocation$numbOfClaims", "$longString-$latString")
            apply()
        }
    }

    private fun sendClaimToServer(
        claimID: Int,
        descString: String,
        longString: String,
        latString: String,
        photoName: String
    ){
        /*val userId = 0
        val status = "na"
        //public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
        queue = Volley.newRequestQueue(this)
        val parameters = "userId=$userId&indexUpdateClaim=$claimID&newClaimDes=$descString&newClaimPho=$photoName&newClaimLoc=$longString-$latString&newClaimSta=$status"
        val url = "http://$ip:$port/postInsertNewClaim?$parameters"
        val jsonRequest = JsonObjectRequest(Request.Method.POST, url, null,
            {
                Log.d("ADD_CLAIM", "SERVER: SUCCESS.$it")
            },
            {
                Log.d("ADD_CLAIM", "SERVER: FAILED TO CONNECT WITH $url")
                //TODO: try again later
            }
        )
        queue.add(jsonRequest)*/

    }


    val REQUEST_IMAGE_CAPTURE = 1

    fun takePhoto(view: View) {
        dispatchTakePictureIntent()
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Log.d("ADD_PHOTO", "this will now take picture and add to files")

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    //...
                    Log.d("ADD_PHOTO_ERROR", "this image did not work.")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("ADD_PHOTO", "this will now Display image")
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val f = File(currentPhotoPath)
            imageView.setImageURI(Uri.fromFile(f))
        }
    }

}