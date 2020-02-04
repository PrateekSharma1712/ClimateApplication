package com.prateek.weatherapplication.framework

import android.location.Geocoder
import com.prateek.weatherapplication.domain.model.Climate
import com.prateek.weatherapplication.domain.model.ClimateForecast
import com.prateek.weatherapplication.framework.network.model.CurrentWeatherResponseDTO
import com.prateek.weatherapplication.framework.network.model.ForecastResponseDTO
import com.prateek.weatherapplication.framework.network.services.WeatherApiService
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val geoCoder: Geocoder
) {

    suspend fun getForecast(latitude: Double, longitude: Double): ClimateForecast? {
        val weatherResponseDeferred =
            weatherApiService.getForecastAsync(latitude, longitude).await()

        return if (weatherResponseDeferred.isSuccessful) {
            parseForecastResponse(weatherResponseDeferred.body())
        } else {
            null
        }
    }

    suspend fun getCurrentWeather(
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double
    ): ClimateForecast? {
        val currentWeatherResponseDeferred =
            weatherApiService.getCurrentWeatherByRectangleAsync(
                floor(minLatitude).toInt().toString() + "," + floor(
                    minLongitude
                ).toInt().toString() + "," + floor(maxLatitude).toInt().toString() + "," + floor(
                    maxLongitude
                ).toInt().toString()
            )
                .await()

        return if (currentWeatherResponseDeferred.isSuccessful) {
            parseCurrentWeatherResponse(currentWeatherResponseDeferred.body())
        } else {
            null
        }
    }

    private fun parseCurrentWeatherResponse(currentWeatherResponseDTO: CurrentWeatherResponseDTO?): ClimateForecast? {
        val climates = mutableListOf<Climate>()
        currentWeatherResponseDTO?.list?.forEach {
            climates.add(
                Climate(
                    minTemperature = it?.main?.tempMin,
                    maxTemperature = it?.main?.tempMax,
                    weatherDescription = it?.weather?.get(0)?.description,
                    windSpeed = it?.wind?.speed,
                    dateTime = it?.dt?.toString()
                )
            )
        }

        return currentWeatherResponseDTO?.let {
            ClimateForecast(climates, "")
        }
    }

    private fun parseForecastResponse(forecastResponseDTO: ForecastResponseDTO?): ClimateForecast? {
        val climates = mutableListOf<Climate>()
        forecastResponseDTO?.list?.forEach {
            climates.add(
                Climate(
                    minTemperature = it?.main?.tempMin,
                    maxTemperature = it?.main?.tempMax,
                    weatherDescription = it?.weather?.get(0)?.description,
                    windSpeed = it?.wind?.speed,
                    dateTime = it?.dtTxt
                )
            )
        }

        return forecastResponseDTO?.let {
            ClimateForecast(climates, forecastResponseDTO?.city?.name)
        }
    }

    /**
     * checks if [Weather] should be fetched from [WeatherApiService] or
     * [WeatherDao]
     * Once weather received, saves to local database
     */
    /*suspend fun getWeather(location: Location): Weather? {
        val lastWeatherTimeStamp = appSharedPreference.getTimeStamp()

        return if (Utils.shouldFetchLatestWeather(lastWeatherTimeStamp)) {
            val weatherResponseDeferred =
                weatherApiService.getWeatherAsync(location.latitude, location.longitude).await()

            if (weatherResponseDeferred.isSuccessful) {
                val weather = parseWeatherResponse(weatherResponseDeferred.body(), location)
                weatherDao.insertWeather(weather)
                saveLastFetchedTimeStamp()
                weather
            } else {
                null
            }
        } else {
            weatherDao.getWeather()
        }
    }*/

    /**
     * parses weather network response [OpenWeatherMapDTO] to [Weather]
     * because, [OpenWeatherMapDTO] has extra fields which are irrelevant to show in
     * UI
     */
    /*private fun parseWeatherResponse(
        openWeatherMapDTO: OpenWeatherMapDTO?,
        location: Location
    ): Weather? {
        return openWeatherMapDTO?.let {
            Weather(
                id = 1, latitude = it.coord?.lat, longitude = it.coord?.lon,
                locationName = location.toSingleLineAddress(geoCoder),
                temperature = it.main?.temp ?: 0.0,
                feelsLikeTemperature = it.main?.feelsLike ?: 0.0,
                temperatureTime = it.dt,
                maxTemperature = it.main?.tempMax ?: 0.0,
                minTemperature = it.main?.tempMin ?: 0.0,
                humidity = it.main?.humidity ?: 0,
                pressure = it.main?.pressure ?: 0,
                windSpeed = it.wind?.speed ?: 0.0,
                type = it.weather?.get(0)?.main ?: "",
                description = it.weather?.get(0)?.description ?: "",
                icon = it.weather?.get(0)?.icon ?: ""
            )
        }
    }

    private fun saveLastFetchedTimeStamp() {
        appSharedPreference.putTimeStamp()
    }
*/
}