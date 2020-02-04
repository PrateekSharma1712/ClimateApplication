package com.prateek.weatheapplication.framework.network.services

import com.prateek.weatheapplication.framework.network.model.ForecastResponseDTO
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val WEATHER_ICON_URL = "http://openweathermap.org/img/wn/"

interface WeatherApiService {

    @GET("forecast")
    fun getForecastAsync(@Query("lat") latitude: Double, @Query("lon") longitude: Double): Deferred<Response<ForecastResponseDTO?>>
}