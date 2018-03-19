package com.hackathon.pesttracker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.gson.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.intentFor
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val REQUEST_IMAGE_CAPTURE = 1

class MainActivity : AppCompatActivity() {

    private var currentPhotoPath = ""

    private lateinit var storageRef: StorageReference
    private lateinit var db: FirebaseDatabase
    private lateinit var locationProvider: LocationProvider

    private var firebasePhotoList = PhotoResponseList()

    private lateinit var adapter: PhotoGalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

        storageRef = FirebaseStorage.getInstance().reference
        db = FirebaseDatabase.getInstance()
        locationProvider = LocationProvider(this)
        initAdapter()
        getImageList()

        image_gallery.apply {
            setHasFixedSize(true)
            val gridLayout = GridLayoutManager(context, 2)
            layoutManager = gridLayout
            clearOnScrollListeners()
        }

        nav_view.setNavigationItemSelectedListener { item: MenuItem ->
            item.isChecked = true
            drawer_layout.closeDrawers()
            when (item.itemId) {
                R.id.nav_map -> launchMapActivity()
            }
            true
        }

        fab.setOnClickListener { view ->
            dispatchTakePictureIntent()
        }
    }

    override fun onResume() {
        super.onResume()
        nav_view.menu.getItem(0).isChecked = true
    }

    private fun initAdapter() {
        if (image_gallery.adapter == null) {
            adapter = PhotoGalleryAdapter(this, { imageResponse ->
                val intent = intentFor<ImageViewActivity>("image" to imageResponse)
                startActivity(intent)
            },
                    2)
        }
        image_gallery.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile = createImageFile()
            photoFile?.let {
                val photoURI = FileProvider.getUriForFile(
                        this,
                        "com.hackathon.pesttracker",
                        it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            saveToFirebase()
            Log.i("onActivityResult", "Image successful")
        }
    }

    private fun saveToFirebase() {
        val imageFile = Uri.fromFile(File(currentPhotoPath))
        val imageRef = storageRef.child("images/${UUID.randomUUID()})")
        Log.i("TAG", "saving file to gcs")
        val location = locationProvider.currentLocation
        val imageMetadata = StorageMetadata.Builder()
                .setContentType("image/png")
                .setCustomMetadata("location", Gson().toJson(location))
                .build()
        imageRef.putFile(imageFile, imageMetadata)
    }

    private fun getImageList() {
        val dbRef = db.getReference("images")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value != null) {
                    val imageFilenameMap: Map<String, Map<String, String>> =
                            dataSnapshot.value as Map<String, Map<String, String>>
                    firebasePhotoList = PhotoResponseList(imageFilenameMap.map {
                        val locationJson = it.value["location"]
                        val location = Gson().fromJson(locationJson, PhotoLocation::class.java)
                        FirebasePhotoResponse(
                                location,
                                it.value.getOrDefault("path", ""),
                                it.value.getOrDefault("thumbnail", ""),
                                it.key
                        )
                    })
                    Log.i("TAG", firebasePhotoList.toString())
                    adapter.setData(firebasePhotoList)
                }
            }
        })
    }

    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss").format(Date())
        val imageFileName = "PNG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, ".png", storageDir
        )
        currentPhotoPath = image.absolutePath
        return image
    }

    private fun launchMapActivity() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        val intent = intentFor<MapsActivity>("photos" to firebasePhotoList)
        startActivity(intent)
    }
}