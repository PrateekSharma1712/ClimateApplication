package com.prateek.weatherapplication.framework.network.model

import com.squareup.moshi.Json

data class CurrentWeatherResponseDTO(
    @Json(name = "cnt")
    val cnt: Int?,
    @Json(name = "cod")
    val cod: String?,
    @Json(name="calctime")
    val calcTime: Double?,
    @Json(name = "list")
    val list: List<CurrentWeather?>?
)