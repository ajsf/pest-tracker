package com.hackathon.pesttracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var photoList: PhotoResponseList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
                actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        nav_view.setNavigationItemSelectedListener { item: MenuItem ->
            item.isChecked = true
            drawer_layout.closeDrawers()
            when (item.itemId) {
                R.id.nav_gallery -> onBackPressed()
            }
            true
        }

        val bundle = intent.extras
        photoList = bundle.getParcelable("photos")
        Log.d("TAG", photoList.toString())
    }

    override fun onResume() {
        super.onResume()
        nav_view.menu.getItem(1).isChecked = true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var loc = LatLng(0.toDouble(),0.toDouble())

        photoList.list.forEach {
            loc = LatLng(it.location.lat, it.location.long)
            mMap.addMarker(MarkerOptions().position(loc))
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,18.0f))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
}
