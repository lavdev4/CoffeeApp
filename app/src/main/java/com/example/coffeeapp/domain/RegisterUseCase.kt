package com.example.coffeeapp.domain

import com.example.coffeeapp.domain.entities.AuthError
import com.example.coffeeapp.domain.entities.AuthResult
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend fun register(login: String, password: String, repeatPassword: String): AuthResult {
        if (login.isBlank()) return AuthResult.Failure(AuthError.LoginIsBlank)
        if (password.isBlank()) return AuthResult.Failure(AuthError.PasswordIsBlank)
        if (repeatPassword.isBlank()) return AuthResult.Failure(AuthError.RepeatPasswordIsBlank)
        if (password != repeatPassword) return AuthResult.Failure(AuthError.PasswordConfirmFailed)

        return when (val result = authRepository.registerUser(login, password)) {
            is NetworkResultEntity.Success -> {
                authRepository.saveToken(result.data)
                AuthResult.Success
            }
            NetworkResultEntity.Failure(NetworkError.Rejected) -> AuthResult.Failure(AuthError.DataRejected)
            else -> AuthResult.Failure(AuthError.AuthNetworkError((result as NetworkResultEntity.Failure).error))
        }
    }
}