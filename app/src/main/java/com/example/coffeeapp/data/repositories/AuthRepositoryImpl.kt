package com.example.coffeeapp.data.repositories

import com.example.coffeeapp.data.mappers.AuthMapper
import com.example.coffeeapp.data.network.ApiService
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import com.example.coffeeapp.domain.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ApplicationScope
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: AuthMapper
) : AuthRepository {

    override suspend fun loginUser(login: String, password: String): NetworkResultEntity<String> {
        val response = withContext(Dispatchers.IO) {
            apiService.login(mapper.mapLoginPasswordToLoginData(login, password))
        }
        return mapper.mapResponseToResultWithToken(response)
    }

    override suspend fun registerUser(login: String, password: String): NetworkResultEntity<String> {
        val response = withContext(Dispatchers.IO) {
            apiService.register(mapper.mapLoginPasswordToLoginData(login, password))
        }
        return mapper.mapResponseToResultWithToken(response)
    }
}