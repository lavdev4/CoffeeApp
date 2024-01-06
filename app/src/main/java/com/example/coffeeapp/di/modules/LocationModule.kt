package com.example.coffeeapp.di.modules

import android.app.Service
import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides

@Module
interface LocationModule {

    companion object {
        @Provides
        fun provideLocationManager(context: Context): LocationManager {
            return context.getSystemService(Service.LOCATION_SERVICE) as LocationManager
        }
    }
}