package com.example.coffeeapp.domain.entities

sealed class NetworkError {
    data object Error : NetworkError()
    data object NoInternet : NetworkError()
    data object Rejected : NetworkError()
    data object Unauthorised : NetworkError()
    data object TokenExpired : NetworkError()
    data object NoTokenProvided : NetworkError()
}