package com.example.coffeeapp.domain

import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.domain.entities.CafeEntity
import com.example.coffeeapp.domain.entities.NetworkResultEntity

interface CafesRepository {
    suspend fun getCafes(token: String): NetworkResultEntity<List<CafeEntity>>
}