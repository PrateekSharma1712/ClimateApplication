package com.prateek.weatherapplication.domain.model

data class Climate(
    val minTemperature: Double?,
    val maxTemperature: Double?,
    val weatherDescription: String?,
    val icon: String?,
    val windSpeed: Double?,
    val dateTime: Long?,
    val dateText: String? = null,
    val city: String? = ""
)