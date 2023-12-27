package com.example.coffeeapp.presentation.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.coffeeapp.domain.GetProductsUseCase
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.entities.ProductEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private var currentLocation: Int? = null

    private val _productsList: MutableLiveData<List<ProductEntity>> = MutableLiveData()
    val productsList: LiveData<List<ProductEntity>>
        get() = _productsList

    private val _selectedProductsList: MutableLiveData<List<ProductEntity>> =
        MutableLiveData(emptyList())
    val selectedProductsList: LiveData<List<ProductEntity>>
        get() = _selectedProductsList

    private val _networkErrors = MutableLiveData<NetworkError>()
    val networkErrors: LiveData<NetworkError>
        get() = _networkErrors.distinctUntilChanged()

    @SuppressLint("NullSafeMutableLiveData")
    fun updateProducts(locationId: Int) {
        if (locationId != currentLocation) {
            viewModelScope.launch {
                when (val result = getProductsUseCase.getProducts(locationId)) {
                    is NetworkResultEntity.Success -> {
                        _productsList.value = result.data
                        _selectedProductsList.value = emptyList()
                        currentLocation = locationId
                    }
                    is NetworkResultEntity.Failure -> _networkErrors.value = result.error
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