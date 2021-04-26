package com.example.putinsurance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.Navigation
import com.example.putinsurance.data.Claim
import com.example.putinsurance.data.DataRepository
import kotlinx.android.synthetic.main.fragment_tab.*
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private val ip = "10.0.2.2"
    private val port = "8080"
    val MAX_CLAIMS = 5
    private lateinit var sharedPref: SharedPreferences
    private val REQUEST_IMAGE_CAPTURE = 1
    private var currentPhotoPath: String  = ""
    private lateinit var dataRepository: DataRepository
    private val mapTag = "map"
    private val photoTag = "photo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPref = getSharedPreferences("com.example.putinsurance", Context.MODE_PRIVATE)
        dataRepository = InjectorUtils.getDataRepository(this)

    }

    /*override fun onStop() {
        super.onStop()
        // Cancelling the requests
        //queue?.cancelAll(TAG)
        // I think this is enough for one activity (no tags)
        //can put queue in activity instead of datarepository?
        queue?.cancelAll(this)
    }*/



    // According to this answer, fragment switches should always be done through the activity in which they reside:
    // https://stackoverflow.com/questions/58891060/android-switch-between-multiple-fragments-in-a-tab
    // According to this blog post you should hide and show the fragments, especially since map fragment is expensive to set up
    // https://medium.com/sweet-bytes/switching-between-fragments-without-the-mindless-killing-spree-9efee5f51924
    // Only works on one tab -> might have to send in the number of the tab to create a unique id.
    // However, stops working on the one tab after opening a few other tabs.
    fun showMap(position: Int?) {
        Log.d("Switch", "Showing map")

        //val mapTag = "map_$position"
        //val photoTag = "photo_$position"

        Log.d("tab", mapTag)
        Log.d("tab", photoTag)

        // According to one answer here (by Patrick Favre) detach and attach is more
        // memory efficient:
        // https://stackoverflow.com/questions/22713128/how-can-i-switch-between-two-fragments-without-recreating-the-fragments-each-ti
        if (supportFragmentManager.findFragmentByTag(mapTag) != null) {

            if (supportFragmentManager.findFragmentByTag(mapTag)?.isAdded == true)
                detachFragment(mapTag)

            Log.d("showMap", "mapTag != null")
            Log.d("showMap != null", "${supportFragmentManager.findFragmentByTag(mapTag)!!.isVisible}")
            attachFragment(mapTag)
        } else {
            Log.d("showMap", "mapTag == null")
            addFragment(MapsFragment(), mapTag)
        }

        if (supportFragmentManager.findFragmentByTag(photoTag) != null) {
            Log.d("showMap", "photoTag != null")
            detachFragment(photoTag)
        }

        /*if (supportFragmentManager.findFragmentById(R.id.fragment_photo) != null) {
            Log.d("showMap", "photoTag != null")
            supportFragmentManager
                .beginTransaction()
                .detach(supportFragmentManager.findFragmentById(R.id.fragment_photo)!!)
                .commit()
        }*/
    }

    fun showPhoto(position: Int?) {
        Log.d("Switch", "Showing photo")

       if (supportFragmentManager.findFragmentByTag(photoTag) != null) {

           if (supportFragmentManager.findFragmentByTag(photoTag)?.isAdded == true)
               detachFragment(photoTag)

            Log.d("showPhoto", "photoTag != null")
            attachFragment(photoTag)
        } else {
            Log.d("showPhoto", "photoTag == null")
            addFragment(PhotoFragment.newInstance(), photoTag)
        }

        if (supportFragmentManager.findFragmentByTag(mapTag) != null) {
            Log.d("showPhoto", "mapTag != null")
            detachFragment(mapTag)
        }

    }

    private fun addFragment(fragment : Fragment, tag : String) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container_view, fragment, tag)
            .commit()
    }

    private fun attachFragment(tag : String) {
        supportFragmentManager
            .beginTransaction()
            .attach(supportFragmentManager.findFragmentByTag(tag)!!)
            .commit()
    }

    private fun detachFragment(tag : String) {
        supportFragmentManager
            .beginTransaction()
            .detach(supportFragmentManager.findFragmentByTag(tag)!!)
            .commit()
    }

    /*fun detachBoth() {

        if (supportFragmentManager.findFragmentByTag(mapTag)?.isAdded == true)
            detachFragment(mapTag)

        if (supportFragmentManager.findFragmentByTag(photoTag)?.isAdded == true)
            detachFragment(photoTag)
    }
*/
    // OnCheckedChangeListener is recommended by stack overflow:
    // https://stackoverflow.com/questions/11278507/android-widget-switch-on-off-event-listener

    /**Login functions*/
    // TODO: check if SINGLETON of shared preferences and queue is recommended
    fun logIn(view: View) {
        // Shared Preferences

        // TODO: Uncomment the lines below and remove this.
        // Was only done to test on personal phone
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_tabFragment)

       /* val emailText =  findViewById<TextView>(R.id.editTextTextEmailAddress).text.toString()
        val passwordHash = passwordToHashMD5(findViewById<TextView>(R.id.editTextTextPassword).text.toString())
        val loginCallBack =  { valid:Boolean ->
            if(valid){
                dataRepository.getAllClaimsFromServer()
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_tabFragment)
            } else {
                Toast.makeText(this,"login failed", Toast.LENGTH_SHORT).show()
            }
        }

        // If email and password are in shared pref, nullpointerexception is not thrown
        dataRepository.userValidation(emailText, passwordHash, loginCallBack)*/

    }



    // Convert to hash using MD5
    // https://www.geeksforgeeks.org/md5-hash-in-java/ USE MD5
    private fun passwordToHashMD5(password : String) : String {
        val bytes = MessageDigest
            .getInstance("MD5")
            .digest(password.toByteArray())

        return BigInteger(1, bytes).toString(16).padStart(32, '0')
    }

    // TODO: delete the rest of the saved data. NB: Check first that all have been pushed to server!
    fun logOut(view: View) {
        dataRepository.deleteFromSharedPreferences()
        Log.d("logIn", "LOGGING OUT")
        Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_loginFragment)
    }
    /**Login functions end*/


    /**Tab functions*/
    fun newClaim(view: View) {
        val numbOfClaims = sharedPref.getInt("numberOfClaims",0)
        if (numbOfClaims >= MAX_CLAIMS) {
            Toast.makeText(this,"claim limit reached", Toast.LENGTH_SHORT).show()
            return
        }
        Navigation.findNavController(view).navigate(R.id.action_tabFragment_to_claimFormFragment)
    }

    fun goToSettings(view: View){
        Navigation.findNavController(view).navigate(R.id.action_tabFragment_to_settingsFragment)
    }
    /**Tab functions end*/


    /** Claim form functions*/
    // TODO (not baseline func) chose photo from gallery
    fun takePhoto(view: View) {
        dispatchTakePictureIntent()
    }

    @Throws(IOException::class)
    private fun createImageFile(fileName: String): File {
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            fileName, /* prefix */
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
                    val nextClaimNumber = sharedPref.getInt("numberOfClaims",0)
                    val filename = "photo${nextClaimNumber}"
                    createImageFile(filename)
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
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ADD_PHOTO", "this will now Display image")
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val f = File(currentPhotoPath)
            val imageView = findViewById<ImageView>(R.id.photoPreviewView)
            imageView.setImageURI(Uri.fromFile(f))
        }
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
        dataRepository.addClaim(numbOfClaims,Claim(numbOfClaims.toString(), descString, photoName,"$longString-$latString","0"),"")
        //dataRepository.insertClaimIntoSharedPreferences(numbOfClaims, descString, longString, latString, photoName,sharedPref)
        //dataRepository.sendClaimToServer(numbOfClaims, descString, longString, latString, photoName)
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
        Navigation.findNavController(view).navigate(R.id.action_claimFormFragment_to_tabFragment)
    }
    /** Claim form functions end*/

    /** settings functions*/
    fun changePassword(view: View){
        Log.d("ADD_CLAIM", "this claim add has started")
        val password: String = findViewById<TextView>(R.id.editTextTextPassword).text.toString()
        val passHash = passwordToHashMD5(password)
        val changePasswordCallBack = {
            Toast.makeText(this,"password is updated", Toast.LENGTH_SHORT).show()
        }

        dataRepository.changePassword(password,passHash,changePasswordCallBack)
    }

    fun sendIndex(int: Int?) {

    }

    /** settings functions end*/

}