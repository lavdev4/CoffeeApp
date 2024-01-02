package com.example.coffeeapp.domain.usecases

import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.repositories.CafesRepository
import com.example.coffeeapp.domain.repositories.TokenRepository
import javax.inject.Inject

class GetCafesUseCase @Inject constructor(
    private val cafesRepository: CafesRepository,
    private val tokenRepository: TokenRepository
) {

    suspend fun getCafes(): NetworkResultEntity<List<CafeEntity>> {
        val token = tokenRepository.getAuthToken()
        return if (token != null) {
            cafesRepository.getCafes(token)
        } else {
            NetworkResultEntity.Failure(NetworkError.NoTokenProvided)
        }
    }
}