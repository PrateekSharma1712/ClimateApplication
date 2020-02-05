package com.prateek.weatherapplication

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsKtTest {

    @Test
    fun extractHourMinuteTest() {
        var actual = "2020-01-01 03:00:00".extractHourMinute()
        var expected = "03:00"
        assertEquals(expected, actual)

        actual = "".extractHourMinute()
        expected = ""
        assertEquals(expected, actual)
    }
}