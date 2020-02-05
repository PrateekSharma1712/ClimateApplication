package com.prateek.weatherapplication.framework.network.model


import com.squareup.moshi.Json

data class ClimateForecast(
    @Json(name = "clouds")
    val clouds: Clouds?,
    @Json(name = "dt")
    val date: Int?,
    @Json(name = "dt_txt")
    val dateText: String?,
    @Json(name = "main")
    val main: Main?,
    @Json(name = "sys")
    val sys: Sys?,
    @Json(name = "weather")
    val weather: List<Weather?>?,
    @Json(name = "wind")
    val wind: Wind?
)