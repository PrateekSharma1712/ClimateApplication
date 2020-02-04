package com.prateek.weatheapplication.di

import android.app.Application
import com.prateek.weatheapplication.ui.LandingActivity
import com.prateek.weatheapplication.ui.LandingViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, WeatherApiModule::class])
interface AppComponent {
    fun inject(activity: LandingActivity)
    fun inject(viewModel: LandingViewModel)

    fun getApplication(): Application
}