package com.prateek.weatheapplication.framework.network.model


import com.squareup.moshi.Json

data class Sys(
    @Json(name = "pod")
    val pod: String?
)