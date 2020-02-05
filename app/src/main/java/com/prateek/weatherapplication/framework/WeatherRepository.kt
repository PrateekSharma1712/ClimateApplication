package com.prateek.weatherapplication.framework

import com.prateek.weatherapplication.domain.model.Climate
import com.prateek.weatherapplication.domain.model.ClimateForecast
import com.prateek.weatherapplication.framework.network.model.CurrentWeatherResponseDTO
import com.prateek.weatherapplication.framework.network.model.ForecastResponseDTO
import com.prateek.weatherapplication.framework.network.services.WeatherApiService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
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

    suspend fun getCurrentWeather(boxString: String): ClimateForecast? {
        val currentWeatherResponseDeferred =
            weatherApiService.getCurrentWeatherByRectangleAsync(boxString)
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
                    icon = it?.weather?.get(0)?.icon,
                    windSpeed = it?.wind?.speed,
                    dateTime = it?.dt?.times(1000L),
                    city = it?.name
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
                    icon = it?.weather?.get(0)?.icon,
                    windSpeed = it?.wind?.speed,
                    dateTime = it?.dt?.times(1000L),
                    dateText = it?.dtTxt
                )
            )
        }

        return forecastResponseDTO?.let {
            ClimateForecast(climates, it.city?.name)
        }
    }
}