package com.example.coffeeapp.data.mappers

import com.example.coffeeapp.data.network.models.AuthResponseDto
import com.example.coffeeapp.data.network.models.LoginDataDto
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.domain.entities.NetworkError
import com.example.coffeeapp.domain.entities.NetworkResultEntity
import retrofit2.Response

import javax.inject.Inject

@ApplicationScope
class AuthMapper @Inject constructor() {

    fun mapLoginPasswordToLoginData(login: String, password: String): LoginDataDto {
        return LoginDataDto(login = login, password = password)
    }

    fun mapResponseToResultWithToken(response: Response<AuthResponseDto>): NetworkResultEntity<String> {
        return if (response.isSuccessful && response.body() != null) {
            NetworkResultEntity.Success(response.body()!!.token)
        } else {
            when (response.code()) {
                401 -> NetworkResultEntity.Failure(NetworkError.Unauthorised)
                406 -> NetworkResultEntity.Failure(NetworkError.Rejected)
                1 -> NetworkResultEntity.Failure(NetworkError.NoInternet)
                else -> NetworkResultEntity.Failure(NetworkError.UnknownError)
            }
        }
    }
}