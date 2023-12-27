package com.example.coffeeapp.domain

import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.AuthResult
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {

    suspend fun logIn(login: String, password: String): AuthResult {
        if (login.isBlank()) return AuthResult.Failure(AuthError.LoginIsBlank)
        if (password.isBlank()) return AuthResult.Failure(AuthError.PasswordIsBlank)

        return when (val result = authRepository.loginUser(login, password)) {
            is NetworkResultEntity.Success -> {
                authRepository.saveToken(result.data)
                AuthResult.Success
            }
            NetworkResultEntity.Failure(NetworkError.Unauthorised) -> AuthResult.Failure(AuthError.DataMismatch)
            else -> AuthResult.Failure(AuthError.AuthNetworkError((result as NetworkResultEntity.Failure).error))
        }
    }
}