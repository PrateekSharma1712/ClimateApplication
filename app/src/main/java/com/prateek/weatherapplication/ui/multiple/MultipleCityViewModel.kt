package com.prateek.weatherapplication.ui.multiple

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prateek.weatherapplication.WeatherApplication
import com.prateek.weatherapplication.domain.model.ClimateForecast
import com.prateek.weatherapplication.framework.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

class MultipleCityViewModel : ViewModel() {

    companion object {
        const val ZOOM_LEVEL: String = ",10"
    }

    @Inject
    lateinit var coroutineScope: CoroutineScope
    @Inject
    lateinit var weatherRepository: WeatherRepository
    private val listOfLatitudes = mutableListOf<Double>()
    private val listOfLongitude = mutableListOf<Double>()

    private val _climateForecastLiveData = MutableLiveData<ClimateForecast>()
    val climateForecastLiveData: MutableLiveData<ClimateForecast>
        get() = _climateForecastLiveData

    init {
        WeatherApplication.application.appComponent.inject(this)
    }

    private fun fetchWeather(selectedLocations: List<Double>) {
        coroutineScope.launch {
            val climateForecast =
                weatherRepository.getCurrentWeather(
                    selectedLocations.joinToString(
                        separator = ",",
                        postfix = ZOOM_LEVEL
                    )
                )
            _climateForecastLiveData.postValue(climateForecast)
        }
    }

    fun fetchWeatherButtonClicked() {
        val minLatitude: Double = listOfLatitudes.minBy { it.absoluteValue } ?: 0.0

        val maxLatitude = listOfLatitudes.maxBy { it.absoluteValue } ?: 0.0

        val minLongitude: Double = listOfLongitude.minBy { it.absoluteValue } ?: 0.0

        val maxLongitude = listOfLongitude.maxBy { it.absoluteValue } ?: 0.0

        fetchWeather(listOf(minLongitude, minLatitude, maxLongitude, maxLatitude))
    }

    fun onPlaceSelected(latitude: Double, longitude: Double) {
        listOfLatitudes.add(latitude)
        listOfLongitude.add(longitude)
    }
}