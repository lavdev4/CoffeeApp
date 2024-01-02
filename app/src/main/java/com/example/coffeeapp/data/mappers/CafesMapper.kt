package com.example.coffeeapp.data.mappers

import com.example.coffeeapp.data.network.models.LocationRespondDto
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import retrofit2.Response
import javax.inject.Inject

class CafesMapper @Inject constructor() {

    fun mapResponseToResultWithCafes(
        response: Response<List<LocationRespondDto>>,
    ): NetworkResultEntity<List<CafeEntity>> {
        return if (response.isSuccessful && response.body() != null) {
            val mapValues = mapLocationRespondDtoToCafeEntity(response.body()!!)
            NetworkResultEntity.Success(mapValues)
        } else {
            when (response.code()) {
                401 -> NetworkResultEntity.Failure(NetworkError.TokenExpired)
                1 -> NetworkResultEntity.Failure(NetworkError.NoInternet)
                else -> NetworkResultEntity.Failure(NetworkError.UnknownError)
            }
        }
    }

    private fun mapLocationRespondDtoToCafeEntity(
        values: List<LocationRespondDto>,
    ): List<CafeEntity> {
        return values.map {
            CafeEntity(
                id = it.id,
                name = it.name,
                latitude = it.point.latitude,
                longitude = it.point.longitude
            )
        }
    }
}