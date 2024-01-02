package com.example.coffeeapp.domain.usecases

import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.entities.ProductEntity
import com.example.coffeeapp.domain.repositories.ProductsRepository
import com.example.coffeeapp.domain.repositories.TokenRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productsRepository: ProductsRepository,
    private val tokenRepository: TokenRepository
) {

    suspend fun getProducts(locationId: Int): NetworkResultEntity<List<ProductEntity>> {
        val token = tokenRepository.getAuthToken()
        return if (token != null) {
            productsRepository.getProducts(locationId, token)
        } else {
            NetworkResultEntity.Failure(NetworkError.NoTokenProvided)
        }
    }
}