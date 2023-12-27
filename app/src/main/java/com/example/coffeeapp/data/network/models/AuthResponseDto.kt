package com.example.coffeeapp.data.network.models

data class AuthResponseDto(
    val token: String,
    val tokenLifetime: Int
)
