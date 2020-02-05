package com.prateek.weatherapplication.ui.forecast

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkRequest: NetworkRequest
    private lateinit var networkAvailabilityCallback: ConnectivityManager.NetworkCallback
    private var multiplePlacesMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        landingViewModel = ViewModelProvider(this).get(LandingViewModel::class.java)

        initializeObservers()
        initialiseToolbar()

        connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

        networkAvailabilityCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                landingViewModel.updateNetworkConnectivity(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                landingViewModel.updateNetworkConnectivity(false)
            }
        }
    }

    private fun initialiseToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun initializeObservers() {
        landingViewModel.isNetworkConnected.observe(this, Observer {
            Log.d("isNetworkConnected", it.toString())
            if (it) {
                checkForLocationPermission()
                loaderFrameLayout?.animate()?.alpha(1f)?.setDuration(500)?.start()
                noNetworkImageView?.animate()?.alpha(0f)?.setDuration(500)?.start()
                weatherByDayRecyclerView?.animate()?.alpha(1f)?.setDuration(500)?.start()
                cityTextView?.animate()?.alpha(1f)?.setDuration(500)?.start()
                multiplePlacesMenuItem?.isVisible = true
            } else {
                loaderFrameLayout?.animate()?.alpha(0f)?.setDuration(500)?.start()
                noNetworkImageView?.animate()?.alpha(1f)?.setDuration(500)?.start()
                weatherByDayRecyclerView?.animate()?.alpha(0f)?.setDuration(500)?.start()
                cityTextView?.animate()?.alpha(0f)?.setDuration(500)?.start()
                multiplePlacesMenuItem?.isVisible = false
            }
        })

        landingViewModel.climateForecastLiveData.observe(this, Observer {

            cityTextView.text = getString(R.string.city_forecast, landingViewModel.city)

            climateForecastAdapter =
                ClimateForecastAdapter(
                    it, false
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
                    Manifest.permission.ACCESS_COARSE_LOCATION
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
                progressBar.visibility = View.GONE
                message.text = getString(R.string.unable_to_fetch_location)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        connectivityManager.registerNetworkCallback(networkRequest, networkAvailabilityCallback)
    }

    override fun onStop() {
        super.onStop()
        connectivityManager.unregisterNetworkCallback(networkAvailabilityCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_multiple_places, menu)
        multiplePlacesMenuItem = menu?.findItem(R.id.multiplePlacesMenuItem)
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