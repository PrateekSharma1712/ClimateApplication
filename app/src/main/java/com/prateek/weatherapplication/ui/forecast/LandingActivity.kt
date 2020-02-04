package com.prateek.weatherapplication.ui.forecast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.prateek.weatherapplication.R
import com.prateek.weatherapplication.WeatherApplication
import com.prateek.weatherapplication.ui.multiple.MultipleCityClimateActivity
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var landingViewModel: LandingViewModel
    private lateinit var climateForecastAdapter: ClimateForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        landingViewModel = ViewModelProvider(this).get(LandingViewModel::class.java)

        checkForLocationPermission()
        initializeObservers()
    }

    private fun initializeObservers() {
        landingViewModel.climateForecastLiveData.observe(this, Observer { climateForecast ->
            val climateMap = climateForecast.climates.groupBy { climate ->
                climate.dateTime?.split(" ")?.get(0)
            }
            Log.d("climate forecast", climateMap.toString())
            val list = mutableListOf<Any>()
            climateMap.keys.forEach { key ->
                key?.let {
                    list.add(it)
                    list.addAll(climateMap[it]?.toList()!!)
                }
            }

            climateForecastAdapter =
                ClimateForecastAdapter(
                    list
                )
            weatherByDayRecyclerView.apply {
                hasFixedSize()
                adapter = climateForecastAdapter
            }
        })
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_multiple_places, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.multiplePlacesMenuItem) {
            startActivity(Intent(this, MultipleCityClimateActivity::class.java))
            true
        } else {
            super.onOptionsItemSelected(item)
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