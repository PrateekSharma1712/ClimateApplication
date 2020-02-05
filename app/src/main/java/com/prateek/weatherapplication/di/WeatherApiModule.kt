package com.prateek.weatherapplication.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.prateek.weatherapplication.framework.network.services.WeatherApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val WEATHER_API_KEY = "4e30012380c160714e99ebb343d553f9"
private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"

@Module
class WeatherApiModule {

    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
            val newUrl = chain.request().url()
                .newBuilder()
                .addQueryParameter("units", "metric")
                .addQueryParameter("appid",
                    WEATHER_API_KEY
                )
                .build()

            val newRequest = chain.request()
                .newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    fun provideHttpLoginInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideWeatherApiClient(authInterceptor: Interceptor, httpLoggingInterceptor: HttpLoggingInterceptor) : OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, weatherApiClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(weatherApiClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun providesWeatherApi(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }
}