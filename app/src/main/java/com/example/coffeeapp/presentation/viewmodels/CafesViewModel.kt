package com.example.coffeeapp.presentation.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.domain.GetCafesUseCase
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("NullSafeMutableLiveData")
class CafesViewModel @Inject constructor(
    private val getCafesUseCase: GetCafesUseCase
) : ViewModel() {

    private val _cafesList = MutableLiveData<List<CafeEntity>>()
    val cafesList: LiveData<List<CafeEntity>>
        get() = _cafesList

    private val _networkErrors = MutableLiveData<NetworkError>()
    val networkErrors: LiveData<NetworkError>
        get() = _networkErrors.distinctUntilChanged()

    init {
        viewModelScope.launch {
            when(val result = getCafesUseCase.getCafes()) {
                is NetworkResultEntity.Success -> _cafesList.value = result.data
                is NetworkResultEntity.Failure -> _networkErrors.value = result.error
            }
        }
    }
}