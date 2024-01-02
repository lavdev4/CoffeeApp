package com.example.coffeeapp.presentation.viewmodels.states

sealed class ScreenState {
    data object Initial : ScreenState()
    data object Loading : ScreenState()
    data object Presenting : ScreenState()
    data object Error : ScreenState()
}