package com.example.coffeeapp.domain.usecases

import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.AuthResult
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.repositories.AuthRepository
import com.example.coffeeapp.domain.repositories.TokenRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) {

    suspend fun logIn(login: String, password: String): AuthResult {
        if (login.isBlank()) return AuthResult.Failure(AuthError.LoginIsBlank)
        if (password.isBlank()) return AuthResult.Failure(AuthError.PasswordIsBlank)

        return when (val result = authRepository.loginUser(login, password)) {
            is NetworkResultEntity.Success -> {
                tokenRepository.setAuthToken(result.data)
                AuthResult.Success
            }
            NetworkResultEntity.Failure(NetworkError.Unauthorised) -> {
                AuthResult.Failure(AuthError.DataMismatch)
            }
            is NetworkResultEntity.Failure -> {
                AuthResult.Failure(
                    AuthError.AuthNetworkError(result.error)
                )
            }
        }
    }
}