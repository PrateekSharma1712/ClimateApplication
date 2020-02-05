package com.prateek.weatherapplication.domain.model

data class Climate(
    val minTemperature: Double?,
    val maxTemperature: Double?,
    val weatherDescription: String?,
    val windSpeed: Double?,
    val dateTime: String?
)