package com.prateek.weatherapplication.ui.multiple

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prateek.weatherapplication.WeatherApplication
import com.prateek.weatherapplication.framework.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.absoluteValue

class MultipleCityViewModel : ViewModel() {

    companion object {
        const val ZOOM_LEVEL: String = ",10"
        const val CITY = "CITY"
    }

    @Inject
    lateinit var coroutineScope: CoroutineScope
    @Inject
    lateinit var weatherRepository: WeatherRepository
    private val listOfLatitudes = mutableListOf<Double>()
    private val listOfLongitude = mutableListOf<Double>()
    private val listOfNames = mutableListOf<String>()
    private val _listOfNamesLiveData = MutableLiveData<MutableList<String>>()
    val listOfNamesLiveData: MutableLiveData<MutableList<String>>
        get() = _listOfNamesLiveData

    private val _apiLoadingStatus = MutableLiveData<ApiLoadingStatus>()
    val apiLoadingStatus: MutableLiveData<ApiLoadingStatus>
        get() = _apiLoadingStatus

    private val _climateForecastLiveData = MutableLiveData<List<Any>>()
    val climateForecastLiveData: MutableLiveData<List<Any>>
        get() = _climateForecastLiveData

    init {
        WeatherApplication.application.appComponent.inject(this)
    }

    enum class ApiLoadingStatus {
        IS_LOADING, LOADED, UNABLE_TO_LOAD_WEATHER
    }

    private fun fetchWeather(selectedLocations: List<Double>) {
        _apiLoadingStatus.postValue(ApiLoadingStatus.IS_LOADING)
        coroutineScope.launch {
            val climateForecast =
                weatherRepository.getCurrentWeather(
                    selectedLocations.joinToString(
                        separator = ",",
                        postfix = ZOOM_LEVEL
                    )
                )

            val list = mutableListOf<Any>()
            list.add(CITY)
            climateForecast?.climates?.forEach {
                list.add(it)
            }

            if (list.size <= 1) {
                _apiLoadingStatus.postValue(ApiLoadingStatus.UNABLE_TO_LOAD_WEATHER)
            } else {
                _climateForecastLiveData.postValue(list)
                _apiLoadingStatus.postValue(ApiLoadingStatus.LOADED)
            }
        }
    }

    fun fetchWeatherButtonClicked() {
        val minLatitude: Double = listOfLatitudes.minBy { it.absoluteValue } ?: 0.0

        val maxLatitude = listOfLatitudes.maxBy { it.absoluteValue } ?: 0.0

        val minLongitude: Double = listOfLongitude.minBy { it.absoluteValue } ?: 0.0

        val maxLongitude = listOfLongitude.maxBy { it.absoluteValue } ?: 0.0

        fetchWeather(listOf(minLongitude, minLatitude, maxLongitude, maxLatitude))
    }

    fun onPlaceSelected(latitude: Double, longitude: Double, name: String) {
        listOfLatitudes.add(latitude)
        listOfLongitude.add(longitude)
        listOfNames.add(name)
        _listOfNamesLiveData.postValue(listOfNames)
    }

    fun onPlaceRemoved(latitude: Double, longitude: Double, name: String) {
        listOfLatitudes.remove(latitude)
        listOfLongitude.remove(longitude)
        listOfNames.remove(name)
        _listOfNamesLiveData.postValue(listOfNames)
    }
}