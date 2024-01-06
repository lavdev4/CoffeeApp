package com.example.coffeeapp.domain.usecases

import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.AuthResult
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.repositories.AuthRepository
import com.example.coffeeapp.domain.repositories.TokenRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) {

    suspend fun register(login: String, password: String, repeatPassword: String): AuthResult {
        if (login.isBlank()) return AuthResult.Failure(AuthError.LoginIsBlank)
        if (password.isBlank()) return AuthResult.Failure(AuthError.PasswordIsBlank)
        if (repeatPassword.isBlank()) return AuthResult.Failure(AuthError.RepeatPasswordIsBlank)
        if (password != repeatPassword) return AuthResult.Failure(AuthError.PasswordConfirmFailed)

        return when (val result = authRepository.registerUser(login, password)) {
            is NetworkResultEntity.Success -> {
                tokenRepository.setAuthToken(result.data)
                AuthResult.Success
            }
            NetworkResultEntity.Failure(NetworkError.Rejected) -> {
                AuthResult.Failure(AuthError.DataRejected)
            }
            is NetworkResultEntity.Failure -> {
                AuthResult.Failure(
                    AuthError.AuthNetworkError(result.error)
                )
            }
        }
    }
}