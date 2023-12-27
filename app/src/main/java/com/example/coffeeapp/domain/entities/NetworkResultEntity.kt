package com.example.coffeeapp.domain.entities

sealed class NetworkResultEntity<out T> {
    data class Success<T>(val data: T): NetworkResultEntity<T>()
    data class Failure(val error: NetworkError): NetworkResultEntity<Nothing>()
}