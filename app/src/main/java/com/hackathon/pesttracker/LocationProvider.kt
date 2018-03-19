package com.hackathon.pesttracker

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

class LocationProvider(activity: Activity) {

    val lm = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    lateinit var currentLocation: PhotoLocation

    val locationListener = object : LocationListener {

        override fun onLocationChanged(location: Location?) {
            location?.let {
                currentLocation = PhotoLocation(it.latitude, it.longitude)
            }
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

    }

    init {
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        val lastLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        currentLocation = PhotoLocation(lastLocation.latitude, lastLocation.longitude)
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0.toLong(), 0f, locationListener)
    }
}