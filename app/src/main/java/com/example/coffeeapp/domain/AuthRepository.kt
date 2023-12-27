package com.example.coffeeapp.domain

import com.example.coffeeapp.data.network.models.AuthResponseDto
import com.example.coffeeapp.domain.entities.NetworkResultEntity

interface AuthRepository {
    suspend fun registerUser(login: String, password: String): NetworkResultEntity<String>
    suspend fun loginUser(login: String, password: String): NetworkResultEntity<String>
    fun saveToken(token: String)
    fun getToken(): String?
}