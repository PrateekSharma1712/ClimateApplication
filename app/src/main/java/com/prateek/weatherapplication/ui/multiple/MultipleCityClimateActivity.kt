package com.prateek.weatherapplication.ui.multiple

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.chip.Chip
import com.prateek.weatherapplication.R
import com.prateek.weatherapplication.ui.forecast.LandingViewModel
import kotlinx.android.synthetic.main.activity_multiple_city_climate.*


class MultipleCityClimateActivity : AppCompatActivity() {

    private lateinit var viewModel: MultipleCityViewModel
    private val selectedLocations = mutableListOf<LatLng>()
    private val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
    private val autoCompleteIntentBuilder = Autocomplete.IntentBuilder(
        AutocompleteActivityMode.OVERLAY, fields
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_city_climate)

        viewModel = ViewModelProvider(this).get(MultipleCityViewModel::class.java)

        val intent = autoCompleteIntentBuilder.build(this)

        placesChipGroup.setOnClickListener {
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        fetchWeatherButton.setOnClickListener {
            val minLatitude = selectedLocations.minBy {
                it.latitude
            }?.latitude ?: 0.0

            val maxLatitude = selectedLocations.maxBy {
                it.latitude
            }?.latitude ?: 0.0

            val minLongitude = selectedLocations.minBy {
                it.longitude
            }?.longitude ?: 0.0

            val maxLongitude = selectedLocations.maxBy {
                it.longitude
            }?.longitude ?: 0.0

            viewModel.fetchWeather(minLatitude, minLongitude, maxLatitude, maxLongitude)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val selectedPlace = Autocomplete.getPlaceFromIntent(it)
                    addChip(selectedPlace.name!!)
                    selectedLocations.add(selectedPlace.latLng!!)
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

    private fun addChip(text: String) {
        val chip = Chip(placesChipGroup.context)
        chip.text = text
        placesChipGroup.addView(chip)
    }

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}
