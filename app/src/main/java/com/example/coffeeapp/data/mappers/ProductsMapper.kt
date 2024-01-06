package com.example.coffeeapp.data.mappers

import com.example.coffeeapp.data.network.models.LocationMenuRespondDto
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.entities.ProductEntity
import retrofit2.Response
import javax.inject.Inject

class ProductsMapper @Inject constructor() {

    fun mapResponseToResultWithProducts(
        response: Response<List<LocationMenuRespondDto>>,
    ): NetworkResultEntity<List<ProductEntity>> {
        return if (response.isSuccessful && response.body() != null) {
            val mapValues = mapLocationMenuRespondDtoToProductEntity(response.body()!!)
            NetworkResultEntity.Success(mapValues)
        } else {
            when (response.code()) {
                401 -> NetworkResultEntity.Failure(NetworkError.TokenExpired)
                /** Custom response from
                 * [com.example.coffeeapp.data.network.interceptors.NetworkConnectionInterceptor] */
                1 -> NetworkResultEntity.Failure(NetworkError.NoInternet)
                else -> NetworkResultEntity.Failure(NetworkError.UnknownError)
            }
        }
    }

    private fun mapLocationMenuRespondDtoToProductEntity(
        values: List<LocationMenuRespondDto>,
    ): List<ProductEntity> {
        return values.map {
            ProductEntity(
                id = it.id,
                name = it.name,
                imageUrl = it.imageUrl,
                price = it.price
            )
        }
    }
}