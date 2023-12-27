package com.example.coffeeapp.presentation

import android.app.Application
import com.example.coffeeapp.BuildConfig
import com.example.coffeeapp.di.ApplicationComponent
import com.example.coffeeapp.di.DaggerApplicationComponent

class CoffeeApplication : Application() {
    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        applicationComponent = DaggerApplicationComponent.builder()
            .context(applicationContext)
            .apiBaseUrl(API_BASE_URL)
            .sharedPreferencesName(SHARED_PREFERENCES_NAME)
            .build()
        super.onCreate()
    }

    companion object {
        private const val API_BASE_URL = BuildConfig.API_BASE_URL
        private const val SHARED_PREFERENCES_NAME = "app_prefs"
    }
}