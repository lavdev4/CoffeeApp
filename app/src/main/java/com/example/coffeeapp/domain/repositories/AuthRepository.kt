package com.example.coffeeapp.domain.repositories

import com.example.coffeeapp.domain.entities.NetworkResultEntity

interface AuthRepository {
    suspend fun loginUser(login: String, password: String): NetworkResultEntity<String>
    suspend fun registerUser(login: String, password: String): NetworkResultEntity<String>
}