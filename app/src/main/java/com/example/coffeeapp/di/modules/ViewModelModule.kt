package com.example.coffeeapp.di.modules

import androidx.lifecycle.ViewModel
import com.example.coffeeapp.di.annotations.ViewModelKey
import com.example.coffeeapp.presentation.viewmodels.CafesViewModel
import com.example.coffeeapp.presentation.viewmodels.GraphsViewModel
import com.example.coffeeapp.presentation.viewmodels.LoginViewModel
import com.example.coffeeapp.presentation.viewmodels.ProductsViewModel
import com.example.coffeeapp.presentation.viewmodels.RegisterViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @IntoMap
    @ViewModelKey(RegisterViewModel::class)
    @Binds
    abstract fun bindRegisterViewModel(impl: RegisterViewModel): ViewModel

    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    @Binds
    abstract fun bindLoginViewModel(impl: LoginViewModel): ViewModel

    @IntoMap
    @ViewModelKey(CafesViewModel::class)
    @Binds
    abstract fun bindCafesViewModel(impl: CafesViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ProductsViewModel::class)
    @Binds
    abstract fun bindProductsViewModel(impl: ProductsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(GraphsViewModel::class)
    @Binds
    abstract fun bindGraphsViewModel(impl: GraphsViewModel): ViewModel
}