package com.example.coffeeapp.presentation.viewmodels.contracts

import androidx.lifecycle.LiveData
import com.example.coffeeapp.domain.entities.AuthError

interface AuthErrorViewModel {
    val authErrors: LiveData<AuthError>
}