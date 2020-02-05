package com.prateek.weatherapplication.framework.network.model


import com.squareup.moshi.Json

data class City(
    @Json(name = "coord")
    val coord: Coord?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "id")
    val id: Int?,
    @Json(name = "name")
    val name: String?
)