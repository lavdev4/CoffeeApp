package com.example.coffeeapp.data

import android.util.Log
import com.example.coffeeapp.data.mappers.CafesMapper
import com.example.coffeeapp.data.network.ApiService
import com.example.coffeeapp.data.network.models.LocationRespondDto
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.domain.CafesRepository
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.lang.Exception
import java.net.ConnectException
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