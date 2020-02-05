package com.prateek.weatherapplication.di

import android.app.Application
import com.prateek.weatherapplication.ui.forecast.LandingViewModel
import com.prateek.weatherapplication.ui.multiple.MultipleCityViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, WeatherApiModule::class])
interface AppComponent {
    fun inject(viewModel: LandingViewModel)
    fun inject(viewModel: MultipleCityViewModel)

    fun getApplication(): Application
}