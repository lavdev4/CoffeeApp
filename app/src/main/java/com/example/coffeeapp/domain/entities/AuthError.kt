package com.example.coffeeapp.domain.entities

sealed class AuthError {
    data object LoginIsBlank : AuthError()
    data object PasswordIsBlank : AuthError()
    data object RepeatPasswordIsBlank : AuthError()
    data object PasswordConfirmFailed : AuthError()
    data object DataRejected : AuthError()
    data object DataMismatch : AuthError()
    data class AuthNetworkError(val networkError: NetworkError) : AuthError()
}