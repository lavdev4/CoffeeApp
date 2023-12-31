package com.example.coffeeapp.domain.repositories

import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.entities.ProductEntity

interface ProductsRepository {
    suspend fun getProducts(
        locationId: Int,
        token: String
    ): NetworkResultEntity<List<ProductEntity>>
}