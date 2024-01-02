package com.example.coffeeapp.presentation.viewmodels.contracts

import androidx.lifecycle.LiveData
import com.example.coffeeapp.presentation.viewmodels.states.ScreenState

interface ScreenStateViewModel {
    val screenState: LiveData<ScreenState>
}