package com.prateek.weatherapplication.ui.forecast

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prateek.weatherapplication.WeatherApplication
import com.prateek.weatherapplication.domain.model.ClimateForecast
import com.prateek.weatherapplication.framework.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LandingViewModel : ViewModel() {

    @Inject
    lateinit var weatherRepository: WeatherRepository
    @Inject
    lateinit var coroutineScope: CoroutineScope

    private val _climateForecastLiveData = MutableLiveData<ClimateForecast>()
    val climateForecastLiveData: MutableLiveData<ClimateForecast>
        get() = _climateForecastLiveData

    init {
        WeatherApplication.application.appComponent.inject(this)
    }

    fun fetchWeather(latitude: Double, longitude: Double) {
        coroutineScope.launch {
            val climateForecast = weatherRepository.getForecast(latitude, longitude)
            _climateForecastLiveData.postValue(climateForecast)
        }
    }
}