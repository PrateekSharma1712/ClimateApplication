package com.prateek.weatheapplication

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.prateek.weatheapplication.di.AppComponent
import com.prateek.weatheapplication.di.AppModule
import com.prateek.weatheapplication.di.DaggerAppComponent
import com.prateek.weatheapplication.di.WeatherApiModule

class WeatherApplication : Application() {

    lateinit var appComponent: AppComponent

    private fun initAppComponent(app: WeatherApplication): AppComponent {
        return DaggerAppComponent.builder().appModule(
            AppModule(
                app
            )
        ).weatherApiModule(WeatherApiModule()).build()
    }

    companion object {
        @get:Synchronized
        lateinit var application: WeatherApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        appComponent = initAppComponent(this)
    }

    fun isConnectedToInternet(): Boolean {
        val connectivityManager: ConnectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting ?: false
    }


}