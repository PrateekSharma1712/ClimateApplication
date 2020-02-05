package com.prateek.weatherapplication.ui.multiple

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.chip.Chip
import com.prateek.weatherapplication.R
import com.prateek.weatherapplication.ui.forecast.ClimateForecastAdapter
import kotlinx.android.synthetic.main.activity_multiple_city_climate.*


class MultipleCityClimateActivity : AppCompatActivity() {

    private lateinit var viewModel: MultipleCityViewModel
    private val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
    private lateinit var autoCompleteIntent: Intent
    private var addPlaceMenuItem: MenuItem? = null

    private lateinit var climateForecastAdapter: ClimateForecastAdapter
    private val autoCompleteIntentBuilder = Autocomplete.IntentBuilder(
        AutocompleteActivityMode.OVERLAY, fields
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_city_climate)

        viewModel = ViewModelProvider(this).get(MultipleCityViewModel::class.java)

        autoCompleteIntent = autoCompleteIntentBuilder.build(this)

        fetchWeatherButton.setOnClickListener {
            viewModel.fetchWeatherButtonClicked()
        }

        initialiseObservers()
        initialiseToolbar()
    }

    private fun initialiseToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_places, menu)
        addPlaceMenuItem = menu?.findItem(R.id.addPlaceMenuItem)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.addPlaceMenuItem) {
            startActivityForResult(autoCompleteIntent, AUTOCOMPLETE_REQUEST_CODE)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.api_key))
            Places.createClient(this)
        }
    }

    override fun onStop() {
        super.onStop()
        Places.deinitialize()
    }

    private fun initialiseObservers() {
        viewModel.climateForecastLiveData.observe(this, Observer { climates ->
            climateForecastAdapter = ClimateForecastAdapter(climates, true)
            multipleCityWeatherRecyclerView.apply {
                hasFixedSize()
                adapter = climateForecastAdapter
            }
        })

        viewModel.listOfNamesLiveData.observe(this, Observer {
            Log.d("names size", it.size.toString())
            if (it.size == 7) {
                addPlaceMenuItem?.isVisible = false
            }

            if (it.size >= 3) {
                fetchWeatherButton.visibility = View.VISIBLE
            } else {
                fetchWeatherButton.visibility = View.GONE
            }

            if (it.isNotEmpty()) {
                addPlaceInstructionTextView.visibility = View.GONE
            }
        })

        viewModel.apiLoadingStatus.observe(this, Observer {
            when (it) {
                MultipleCityViewModel.ApiLoadingStatus.IS_LOADING -> {
                    loaderFrameLayout.animate().alpha(1f).setDuration(500).start()
                }
                MultipleCityViewModel.ApiLoadingStatus.LOADED -> {
                    loaderFrameLayout.animate().alpha(0f).setDuration(500).start()
                }
                else -> {
                    progressBar.visibility = View.GONE
                    message.text = getString(R.string.no_places_found)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val selectedPlace = Autocomplete.getPlaceFromIntent(it)
                    selectedPlace.name?.let { name ->
                        addChip(
                            selectedPlace.latLng?.latitude ?: 0.0,
                            selectedPlace.latLng?.longitude ?: 0.0,
                            name
                        )
                        viewModel.onPlaceSelected(
                            selectedPlace.latLng?.latitude ?: 0.0,
                            selectedPlace.latLng?.longitude ?: 0.0, name
                        )
                    }
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                data?.let {
                    Log.i(
                        "onActivityResult",
                        Autocomplete.getStatusFromIntent(it).statusMessage ?: "No status message"
                    )
                }
            }
        }
    }

    private fun addChip(latitude: Double, longitude: Double, text: String) {
        val chip = Chip(placesChipGroup.context)
        chip.text = text
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            placesChipGroup.removeView(chip)
            viewModel.onPlaceRemoved(latitude, longitude, text)
        }
        placesChipGroup.addView(chip)
    }

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}
