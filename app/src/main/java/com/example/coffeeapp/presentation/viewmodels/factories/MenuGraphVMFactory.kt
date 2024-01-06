package com.example.coffeeapp.presentation.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.presentation.viewmodels.contracts.MenuGraphViewModel
import javax.inject.Inject
import javax.inject.Provider

@ApplicationScope
class MenuGraphVMFactory @Inject constructor(
    val viewModels: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<MenuGraphViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModels[modelClass]?.get() as T
    }
}