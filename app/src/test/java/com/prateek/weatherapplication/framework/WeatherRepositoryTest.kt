package com.prateek.weatherapplication.framework

import android.location.Location
import com.nhaarman.mockito_kotlin.verify
import com.prateek.weatherapplication.framework.network.model.CurrentWeatherResponseDTO
import com.prateek.weatherapplication.framework.network.model.ForecastResponseDTO
import com.prateek.weatherapplication.framework.network.services.WeatherApiService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class WeatherRepositoryTest {

    @get:Rule
    var mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var weatherApiService: WeatherApiService

    lateinit var weatherRepository: WeatherRepository

    @Mock
    lateinit var location: Location

    var forecastResponseDTO = ForecastResponseDTO(
        null, 0, "", emptyList(), 0.0
    )

    var currentWeatherResponseDTO = CurrentWeatherResponseDTO(
        cnt = null,
        calculationTime = 0.0,
        cod = "",
        list = emptyList()
    )

    private lateinit var forecastResponseDTOResponse: Response<ForecastResponseDTO>
    private lateinit var currentWeatherResponseDTOResponse: Response<CurrentWeatherResponseDTO>

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        location.apply {
            latitude = 18.594594594594593
            longitude = 73.76030888904222
        }
        weatherRepository = WeatherRepository(weatherApiService)

        forecastResponseDTOResponse = Response.success(forecastResponseDTO)
        currentWeatherResponseDTOResponse = Response.success(currentWeatherResponseDTO)

        Mockito.`when`(
            weatherApiService.getForecastAsync(
                location.latitude,
                location.longitude
            )
        ).thenReturn(
            GlobalScope.async { forecastResponseDTOResponse }
        )

        Mockito.`when`(
            weatherApiService.getCurrentWeatherByRectangleAsync("73,18,74,19")
        ).thenReturn(
            GlobalScope.async { currentWeatherResponseDTOResponse }
        )
    }

    @Test
    fun getForecastTest() {
        runBlocking {
            weatherRepository.getForecast(location.latitude, location.longitude)
        }

        verify(weatherApiService).getForecastAsync(location.latitude, location.longitude)
    }

    @Test
    fun getCurrentWeatherTest() {
        runBlocking {
            weatherRepository.getCurrentWeather("73,18,74,19")
        }

        verify(weatherApiService).getCurrentWeatherByRectangleAsync("73,18,74,19")
    }
}