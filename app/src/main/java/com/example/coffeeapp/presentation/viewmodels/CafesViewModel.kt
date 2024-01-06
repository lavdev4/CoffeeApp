package com.example.coffeeapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.LocationResultEntity
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.usecases.GetCafesUseCase
import com.example.coffeeapp.presentation.viewmodels.contracts.CafesGraphViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.LocationStateViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.NetworkErrorViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.ScreenStateViewModel
import com.example.coffeeapp.presentation.viewmodels.states.Event
import com.example.coffeeapp.presentation.viewmodels.states.ScreenState
import kotlinx.coroutines.launch
import javax.inject.Inject

class CafesViewModel @Inject constructor(
    private val getCafesUseCase: GetCafesUseCase,
) : ViewModel(),
    CafesGraphViewModel,
    ScreenStateViewModel,
    LocationStateViewModel,
    NetworkErrorViewModel {

    private val _screenState = MutableLiveData<Event<ScreenState>>(Event(ScreenState.Initial))
    override val screenState: LiveData<Event<ScreenState>>
        get() = _screenState

    private val _networkErrors = MediatorLiveData<Event<NetworkError>>()
    override val networkErrors: LiveData<Event<NetworkError>>
        get() = _networkErrors

    private val _locationState = MutableLiveData<Event<LocationResultEntity>>()
    override val locationState: LiveData<Event<LocationResultEntity>>
        get() = _locationState

    private val _cafesList = MediatorLiveData<List<CafeEntity>>()
    val cafesList: LiveData<List<CafeEntity>>
        get() = _cafesList

    var mapWasInitialised = false

    init {
        _networkErrors.addSource(getCafesUseCase.networkErrors) {
            _screenState.value = Event(ScreenState.Error)
            _networkErrors.value = Event(it)
        }
        _cafesList.addSource(getCafesUseCase.cafesData) {
            _screenState.value = Event(ScreenState.Presenting)
            _cafesList.value = it
        }
        viewModelScope.launch {
            _screenState.value = Event(ScreenState.Loading)
            getCafesUseCase.initialise()
        }
        requestLocations()
    }

    fun requestLocations() {
        _locationState.value = Event(getCafesUseCase.requestLocations())
    }
}