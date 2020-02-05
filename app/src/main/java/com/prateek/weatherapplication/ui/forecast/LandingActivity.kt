package com.prateek.weatherapplication.ui.forecast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.prateek.weatherapplication.R
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
        initialiseToolbar()
    }

    private fun initialiseToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun initializeObservers() {
        landingViewModel.climateForecastLiveData.observe(this, Observer { climateForecast ->

            cityTextView.text = getString(R.string.city_forecast, climateForecast?.city)

            val climateMap = climateForecast.climates.groupBy { climate ->
                climate.dateText?.split(" ")?.get(0)
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

        landingViewModel.apiLoadingStatus.observe(this, Observer {
            when (it) {
                LandingViewModel.ApiLoadingStatus.IS_LOADING -> {
                    loaderFrameLayout.animate().alpha(1f).setDuration(500).start()
                }
                LandingViewModel.ApiLoadingStatus.LOADED -> {
                    loaderFrameLayout.animate().alpha(0f).setDuration(500).start()
                }
                LandingViewModel.ApiLoadingStatus.UNABLE_TO_FETCH_LOCATION -> {
                    showSnackBar(R.string.unable_to_fetch_location)
                    progressBar.visibility = View.GONE
                    message.text = getString(R.string.no_location_permission_granted)
                }
                else -> {
                    progressBar.visibility = View.GONE
                    message.text = getString(R.string.connect_to_internet)
                }
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
     * it not null - ask [LandingViewModel] to [LandingViewModel.fetchForecast]
     */
    private fun fetchLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("Location", location.latitude.toString() + location.longitude.toString())
                landingViewModel.fetchForecast(location.latitude, location.longitude)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_LOCATION ->
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    fetchLastKnownLocation()
                } else {
                    landingViewModel.locationPermissionNotGranted()
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