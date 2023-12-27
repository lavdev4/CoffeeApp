package com.example.coffeeapp.domain

import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.entities.ProductEntity
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val authRepository: AuthRepository
) {

    suspend fun getProducts(locationId: Int): NetworkResultEntity<List<ProductEntity>> {
        val token = authRepository.getToken()
        return if (token != null) {
            productsRepository.getProducts(locationId, token)
        } else {
            NetworkResultEntity.Failure(NetworkError.NoTokenProvided)
        }
    }
}