package com.prateek.weatherapplication.framework.network.model


import com.squareup.moshi.Json

data class ForecastResponseDTO(
    @Json(name = "city")
    val city: City?,
    @Json(name = "cnt")
    val count: Int?,
    @Json(name = "cod")
    val cod: String?,
    @Json(name = "list")
    val list: List<ClimateForecast?>?,
    @Json(name = "message")
    val message: Double?
)