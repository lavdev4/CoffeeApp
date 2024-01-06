package com.example.coffeeapp.presentation.viewmodels.contracts

import androidx.lifecycle.LiveData
import com.example.coffeeapp.domain.entities.LocationResultEntity
import com.example.coffeeapp.presentation.viewmodels.states.Event

interface LocationStateViewModel {
    val locationState: LiveData<Event<LocationResultEntity>>
}