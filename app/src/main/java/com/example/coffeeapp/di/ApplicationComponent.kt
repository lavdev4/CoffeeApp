package com.example.coffeeapp.di

import android.content.Context
import com.example.coffeeapp.di.annotations.ApiBaseUrl
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.di.annotations.PreferencesName
import com.example.coffeeapp.di.modules.DataModule
import com.example.coffeeapp.di.modules.LocationModule
import com.example.coffeeapp.di.modules.NetworkModule
import com.example.coffeeapp.di.modules.PreferencesModule
import com.example.coffeeapp.di.modules.ViewModelModule
import com.example.coffeeapp.presentation.CoffeeApplication
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [
    ViewModelModule::class,
    NetworkModule::class,
    PreferencesModule::class,
    DataModule::class,
    LocationModule::class
])
interface ApplicationComponent {

    fun inject(app: CoffeeApplication)

    fun activitySubcomponent(): MainActivitySubcomponent.MainActivitySubcomponentBuilder

    @Component.Builder
    interface ApplicationComponentBuilder {

        @BindsInstance
        fun context(context: Context) : ApplicationComponentBuilder

        @BindsInstance
        fun apiBaseUrl(@ApiBaseUrl url: String): ApplicationComponentBuilder

        @BindsInstance
        fun sharedPreferencesName(@PreferencesName name: String): ApplicationComponentBuilder

        fun build(): ApplicationComponent
    }
}