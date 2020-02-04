package com.prateek.weatheapplication.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.prateek.weatheapplication.R
import com.prateek.weatheapplication.WeatherApplication
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var landingViewModel: LandingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        (application as WeatherApplication).appComponent.inject(this)
        landingViewModel = ViewModelProvider(this).get(LandingViewModel::class.java)

        checkForLocationPermission()
    }

    /**
     * checks for location permission if granted or not, when app is launched
     * if granted - fetch last known location to system
     * it not granted - ask user for location permission
     */
    private fun checkForLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                PERMISSION_REQUEST_LOCATION
            )
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fetchLastKnownLocation()
        }
    }

    /**
     * fetch user's last known location
     * if location null - notify user
     * it not null - ask [LandingViewModel] to [LandingViewModel.fetchWeather]
     */
    private fun fetchLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("Location", location.latitude.toString() + location.longitude.toString())
                //Fetch Weather for 5days/3 hours
                landingViewModel.fetchWeather(location.latitude, location.longitude)
            } else {
                showSnackBar(R.string.unable_to_fetch_location)
            }
        }
    }

    private fun showSnackBar(messageStringResource: Int) {
        Snackbar.make(container, messageStringResource, Snackbar.LENGTH_SHORT)
            .show()
    }

    companion object {
        const val PERMISSION_REQUEST_LOCATION = 1000
    }
}