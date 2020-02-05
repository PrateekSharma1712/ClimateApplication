package com.prateek.weatherapplication

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.util.Log
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt

/**
 * returns a string with format {[Address.getSubLocality],[Address.getSubAdminArea]}
 * from a given location
 */
fun Location.toSingleLineAddress(geoCoder: Geocoder): String {
    var addresses: List<Address> = emptyList()
    try {
        addresses = geoCoder.getFromLocation(
            this.latitude,
            this.longitude,
            1
        )
    } catch (ioException: IOException) {
        Log.e("HomePageViewModel", ioException.message, ioException)
    } catch (illegalArgumentException: IllegalArgumentException) {
        Log.e(
            "HomePageViewModel", "Illegal arguments. Latitude = $this.latitude , " +
                    "Longitude =  $this.longitude", illegalArgumentException
        )
    }

    if (addresses.isNotEmpty()) {
        val address = addresses[0]
        return String.format("%s, %s", address.subLocality, address.subAdminArea)
    }

    return ""
}

/**
 * returns a formatted temperature with degree celsius appended to temperature value
 * being rounded to whole number
 */
fun Double.toDegreeCelsiusFormat(): String {
    val temperatureStringBuilder =
        SpannableStringBuilder(this.roundToInt().toString() + 0x00B0.toChar() + "c")
    temperatureStringBuilder.setSpan(
        SuperscriptSpan(),
        temperatureStringBuilder.length - 3,
        temperatureStringBuilder.length,
        0
    )

    temperatureStringBuilder.setSpan(
        RelativeSizeSpan(0.2f),
        temperatureStringBuilder.length - 3,
        temperatureStringBuilder.length,
        0
    )

    return temperatureStringBuilder.toString()
}

/**
 * returns formatted speed in mps
 */
fun Double.toSpeedFormatInMPS(): String {
    return "$this m/s"
}

/**
 * returns formatted value in percentage
 */
fun Int.toPercentageFormat(): String {
    return "$this %"
}

fun String.extractHourMinute() =  this.split(" ")[1].substring(0, 5)