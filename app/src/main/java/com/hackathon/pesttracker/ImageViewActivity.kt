package com.hackathon.pesttracker

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.content_image_view.*
import org.jetbrains.anko.longToast

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        toolbar.title = "Pest Tracker"
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)


        nav_view.setNavigationItemSelectedListener { item: MenuItem ->
            item.isChecked = true
            drawer_layout.closeDrawers()
            when (item.itemId) {
                R.id.nav_gallery -> onBackPressed()
            }
            true
        }


        val photoResponse = intent.getParcelableExtra<FirebasePhotoResponse>("image")
        image_view.loadImage(photoResponse.path)

        btn_no_pests.setOnClickListener {
            deletePhotoEntry(photoResponse)
            longToast("No pests, photo removed.")
        }


        btn_pests_removed.setOnClickListener {
            deletePhotoEntry(photoResponse)
            longToast("Photo entry deleted.")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun deletePhotoEntry(photoResponse: FirebasePhotoResponse) {
        val db = FirebaseDatabase.getInstance()
        val ref = db.getReference("images/${photoResponse.fbId}")
        ref.removeValue()
        finish()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}