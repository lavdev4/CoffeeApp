package com.example.coffeeapp.presentation.viewmodels.contracts

import androidx.lifecycle.LiveData
import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.presentation.viewmodels.states.Event

interface AuthErrorViewModel {
    val authErrors: LiveData<Event<AuthError>>
}