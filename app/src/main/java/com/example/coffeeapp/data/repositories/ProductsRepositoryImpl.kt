package com.example.coffeeapp.data.repositories

import com.example.coffeeapp.data.mappers.ProductsMapper
import com.example.coffeeapp.data.network.ApiService
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.entities.ProductEntity
import com.example.coffeeapp.domain.repositories.ProductsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ApplicationScope
class ProductsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: ProductsMapper
) : ProductsRepository {

    override suspend fun getProducts(
        locationId: Int,
        token: String
    ): NetworkResultEntity<List<ProductEntity>> {
        val response = withContext(Dispatchers.IO) {
            apiService.getLocationsMenu(locationId.toString(), token)
        }
        return mapper.mapResponseToResultWithProducts(response)
    }
}