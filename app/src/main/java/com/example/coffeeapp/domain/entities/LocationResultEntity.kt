package com.example.coffeeapp.domain.entities

sealed class LocationResultEntity {
    data object Success : LocationResultEntity()
    data object NoPermission : LocationResultEntity()
    data object NoProvider : LocationResultEntity()
}