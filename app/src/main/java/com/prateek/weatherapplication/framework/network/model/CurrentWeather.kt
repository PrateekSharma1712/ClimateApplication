package com.prateek.weatherapplication.framework.network.model

import com.squareup.moshi.Json

data class CurrentWeather(
    @Json(name = "name")
    val name: String?,
    @Json(name = "coord")
    val coordinates: Coord?,
    @Json(name = "main")
    val main: Main?,
    @Json(name = "dt")
    val dt: Int?,
    @Json(name = "wind")
    val wind: Wind?,
    @Json(name = "clouds")
    val clouds: Clouds?,
    @Json(name = "weather")
    val weather: List<Weather?>?
)