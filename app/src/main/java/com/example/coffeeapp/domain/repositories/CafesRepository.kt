package com.example.coffeeapp.domain.repositories

import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkResultEntity

interface CafesRepository {
    suspend fun getCafes(token: String): NetworkResultEntity<List<CafeEntity>>
}