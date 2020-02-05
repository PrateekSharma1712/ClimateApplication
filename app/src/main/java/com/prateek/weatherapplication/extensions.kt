package com.prateek.weatherapplication

fun String?.extractHourMinute(): String? {
    return if (!this.isNullOrBlank()) {
        this.split(" ")[1].substring(0, 5)
    } else {
        ""
    }
}