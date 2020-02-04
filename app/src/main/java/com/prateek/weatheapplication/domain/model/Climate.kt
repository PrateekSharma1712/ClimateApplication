package com.prateek.weatheapplication.domain.model

data class Climate(
    private val minTemperature: Double?,
    private val maxTemperature: Double?,
    val weatherDescription: String?,
    val windSpeed: Double?
)