package com.example.coffeeapp.domain

import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import javax.inject.Inject

class GetCafesUseCase @Inject constructor(
    private val cafesRepository: CafesRepository,
    private val authRepository: AuthRepository
) {

    suspend fun getCafes(): NetworkResultEntity<List<CafeEntity>> {
        val token = authRepository.getToken()
        return if (token != null) {
            cafesRepository.getCafes(token)
        } else {
            NetworkResultEntity.Failure(NetworkError.NoTokenProvided)
        }
    }
}