package com.example.coffeeapp.presentation.viewmodels.contracts

import androidx.lifecycle.LiveData
import com.example.coffeeapp.domain.entities.NetworkError

interface NetworkErrorViewModel {
    val networkErrors: LiveData<NetworkError>
}