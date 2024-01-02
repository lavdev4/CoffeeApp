package com.example.coffeeapp.data.repositories

import com.example.coffeeapp.data.mappers.CafesMapper
import com.example.coffeeapp.data.network.ApiService
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.repositories.CafesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ApplicationScope
class CafesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: CafesMapper
) : CafesRepository {

    override suspend fun getCafes(token: String): NetworkResultEntity<List<CafeEntity>> {
        val response = withContext(Dispatchers.IO) {
            apiService.getLocations(token)
        }
        return mapper.mapResponseToResultWithCafes(response)
    }
}