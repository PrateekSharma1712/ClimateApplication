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

class MultipleCityViewModel : ViewModel() {


    @Inject
    lateinit var coroutineScope: CoroutineScope
    @Inject
    lateinit var weatherRepository: WeatherRepository

    init {
        WeatherApplication.application.appComponent.inject(this)
    }

    fun fetchWeather(
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double
    ) {
        coroutineScope.launch {
            val climateForecast = weatherRepository.getCurrentWeather(
                minLatitude,
                minLongitude,
                maxLatitude,
                maxLongitude
            )

            Log.d("current weather", climateForecast.toString())

        }
    }
}