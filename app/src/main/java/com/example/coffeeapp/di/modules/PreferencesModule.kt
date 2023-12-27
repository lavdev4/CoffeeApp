package com.example.coffeeapp.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.coffeeapp.di.annotations.PreferencesName
import dagger.Module
import dagger.Provides

@Module
interface PreferencesModule {

    companion object {
        @Provides
        fun provideSharedPreferences(
            context: Context,
            @PreferencesName sharedPreferencesName: String
        ): SharedPreferences {
            return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
        }
    }
}