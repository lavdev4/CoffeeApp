package com.example.coffeeapp.presentation.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.entities.ProductEntity
import com.example.coffeeapp.domain.usecases.GetProductsUseCase
import com.example.coffeeapp.presentation.viewmodels.contracts.MenuGraphViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.NetworkErrorViewModel
import com.example.coffeeapp.presentation.viewmodels.contracts.ScreenStateViewModel
import com.example.coffeeapp.presentation.viewmodels.states.Event
import com.example.coffeeapp.presentation.viewmodels.states.ScreenState
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel(),
    MenuGraphViewModel,
    ScreenStateViewModel,
    NetworkErrorViewModel {

    private val _screenState = MutableLiveData<Event<ScreenState>>(Event(ScreenState.Initial))
    override val screenState: LiveData<Event<ScreenState>>
        get() = _screenState

    private val _networkErrors = MutableLiveData<Event<NetworkError>>()
    override val networkErrors: LiveData<Event<NetworkError>>
        get() = _networkErrors

    private val _productsList: MutableLiveData<List<ProductEntity>> = MutableLiveData()
    val productsList: LiveData<List<ProductEntity>>
        get() = _productsList

    private val _selectedProductsList: MutableLiveData<List<ProductEntity>> = MutableLiveData(emptyList())
    val selectedProductsList: LiveData<List<ProductEntity>>
        get() = _selectedProductsList

    private var currentLocation: Int? = null

    @SuppressLint("NullSafeMutableLiveData")
    fun initialise(locationId: Int) {
        if (locationId != currentLocation) {
            _productsList.value = emptyList()
            _selectedProductsList.value = emptyList()

            viewModelScope.launch {
                _screenState.value = Event(ScreenState.Loading)
                when (val result = getProductsUseCase.getProducts(locationId)) {
                    is NetworkResultEntity.Success -> {
                        _productsList.value = result.data
                        _screenState.value = Event(ScreenState.Presenting)
                        currentLocation = locationId
                    }
                    is NetworkResultEntity.Failure -> {
                        _networkErrors.value = Event(result.error)
                        _screenState.value = Event(ScreenState.Error)
                    }
                }
            }
        }
    }

    fun updateSelectedProducts(itemId: Int, quantity: Int) {
        _productsList.value?.let { currentList ->
            val newList = mutableListOf<ProductEntity>()
            currentList.forEach {
                if (it.id == itemId) newList.add(it.copy(count = quantity))
                else newList.add(it.copy())
            }
            _productsList.value = newList
            _selectedProductsList.value = newList.filter { it.count > 0 }
        }
    }
}