package com.prateek.weatheapplication.ui

import androidx.lifecycle.ViewModel
import com.prateek.weatheapplication.WeatherApplication
import com.prateek.weatheapplication.framework.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LandingViewModel : ViewModel() {

    @Inject
    lateinit var weatherRepository: WeatherRepository
    @Inject
    lateinit var coroutineScope: CoroutineScope

    init {
        WeatherApplication.application.appComponent.inject(this)
    }

    fun fetchWeather(latitude: Double, longitude: Double) {
        coroutineScope.launch {
            weatherRepository.getForecast(latitude, longitude)
        }
    }
}