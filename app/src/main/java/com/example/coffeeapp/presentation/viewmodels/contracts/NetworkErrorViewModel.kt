package com.example.coffeeapp.presentation.viewmodels.contracts

import androidx.lifecycle.LiveData
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.presentation.viewmodels.states.Event

interface NetworkErrorViewModel {
    val networkErrors: LiveData<Event<NetworkError>>
}