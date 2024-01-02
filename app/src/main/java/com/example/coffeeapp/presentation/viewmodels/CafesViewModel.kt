package com.example.coffeeapp.presentation.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.usecases.GetCafesUseCase
import com.example.coffeeapp.presentation.viewmodels.contracts.CafesGraphViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.NetworkErrorViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.ScreenStateViewModel
import com.example.coffeeapp.presentation.viewmodels.states.ScreenState
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
class CafesViewModel @Inject constructor(
    private val getCafesUseCase: GetCafesUseCase
) : ViewModel(),
    CafesGraphViewModel,
    ScreenStateViewModel,
    NetworkErrorViewModel {

    private val _screenState = MutableLiveData<ScreenState>(ScreenState.Initial)
    override val screenState: LiveData<ScreenState>
        get() = _screenState

    private val _cafesList = MutableLiveData<List<CafeEntity>>()
    val cafesList: LiveData<List<CafeEntity>>
        get() = _cafesList

    private val _networkErrors = MutableLiveData<NetworkError>()
    override val networkErrors: LiveData<NetworkError>
        get() = _networkErrors.distinctUntilChanged()

    init {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading
            when(val result = getCafesUseCase.getCafes()) {
                is NetworkResultEntity.Success -> {
                    _cafesList.value = result.data
                    _screenState.value = ScreenState.Presenting
                }
                is NetworkResultEntity.Failure -> {
                    _networkErrors.value = result.error
                    _screenState.value = ScreenState.Error
                }
            }
        }
    }
}