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

    private val _apiLoadingStatus = MutableLiveData<ApiLoadingStatus>()
    val apiLoadingStatus: MutableLiveData<ApiLoadingStatus>
        get() = _apiLoadingStatus

    enum class ApiLoadingStatus {
        IS_LOADING, LOADED, UNABLE_TO_LOAD_WEATHER, UNABLE_TO_FETCH_LOCATION
    }

    init {
        WeatherApplication.application.appComponent.inject(this)
    }

    fun fetchForecast(latitude: Double, longitude: Double) {
        _apiLoadingStatus.postValue(ApiLoadingStatus.IS_LOADING)
        coroutineScope.launch {
            val climateForecast = weatherRepository.getForecast(latitude, longitude)
            climateForecast?.let {
                _climateForecastLiveData.postValue(it)
                _apiLoadingStatus.postValue(ApiLoadingStatus.LOADED)
                return@launch
            }
            _apiLoadingStatus.postValue(ApiLoadingStatus.UNABLE_TO_LOAD_WEATHER)
        }
    }

    fun locationPermissionNotGranted() {
        _apiLoadingStatus.postValue(ApiLoadingStatus.UNABLE_TO_FETCH_LOCATION)
    }
}