package com.example.coffeeapp.presentation

import android.app.Application
import com.example.coffeeapp.BuildConfig
import com.example.coffeeapp.di.ApplicationComponent
import com.example.coffeeapp.di.DaggerApplicationComponent
import com.yandex.mapkit.MapKitFactory

class CoffeeApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        applicationComponent = DaggerApplicationComponent.builder()
            .context(applicationContext)
            .apiBaseUrl(API_BASE_URL)
            .sharedPreferencesName(SHARED_PREFERENCES_NAME)
            .build()
        MapKitFactory.setApiKey(Y_MAPS_API_KEY)
        MapKitFactory.setLocale("ru_RU")
        super.onCreate()
    }

    companion object {
        private const val API_BASE_URL = BuildConfig.API_BASE_URL
        private const val Y_MAPS_API_KEY = BuildConfig.Y_MAPS_API_KEY
        private const val SHARED_PREFERENCES_NAME = "app_prefs"
    }
}