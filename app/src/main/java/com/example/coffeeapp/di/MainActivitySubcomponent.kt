package com.example.coffeeapp.di

import androidx.lifecycle.ViewModelProvider
import com.example.coffeeapp.di.annotations.MainActivityScope
import com.example.coffeeapp.presentation.MainActivity
import com.example.coffeeapp.presentation.screens.CafesScreen
import com.example.coffeeapp.presentation.screens.CartScreen
import com.example.coffeeapp.presentation.screens.LoginScreen
import com.example.coffeeapp.presentation.screens.MenuScreen
import com.example.coffeeapp.presentation.screens.RegisterScreen
import dagger.Subcomponent

@MainActivityScope
@Subcomponent
interface MainActivitySubcomponent {

    fun inject(impl: MainActivity)

    fun inject(impl: RegisterScreen)

    fun inject(impl: LoginScreen)

    fun inject(impl: CafesScreen)

    fun inject(impl: MenuScreen)

    fun inject(impl: CartScreen)

    @Subcomponent.Builder
    interface MainActivitySubcomponentBuilder {
        fun build(): MainActivitySubcomponent
    }
}