package com.example.coffeeapp.domain.entities

sealed class AuthResult {
    data object Success : AuthResult()
    data class Failure(val error: AuthError) : AuthResult()
}