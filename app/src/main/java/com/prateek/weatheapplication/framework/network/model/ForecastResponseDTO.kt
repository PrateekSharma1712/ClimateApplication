package com.prateek.weatheapplication.framework.network.model


import com.squareup.moshi.Json

data class ForecastResponseDTO(
    @Json(name = "city")
    val city: City?,
    @Json(name = "cnt")
    val cnt: Int?,
    @Json(name = "cod")
    val cod: String?,
    @Json(name = "list")
    val list: List<ClimateForecast?>?,
    @Json(name = "message")
    val message: Double?
)