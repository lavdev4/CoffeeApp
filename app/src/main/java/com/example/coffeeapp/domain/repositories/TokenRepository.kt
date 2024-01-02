package com.example.coffeeapp.domain.repositories

interface TokenRepository {
    fun setAuthToken(token: String)
    fun getAuthToken(): String?
}