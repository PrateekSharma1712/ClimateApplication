package com.prateek.weatheapplication.framework.network.model


import com.squareup.moshi.Json

data class Clouds(
    @Json(name = "all")
    val all: Int?
)