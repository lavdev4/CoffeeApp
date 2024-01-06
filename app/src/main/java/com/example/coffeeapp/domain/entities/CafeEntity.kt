package com.example.coffeeapp.domain.entities

import java.math.BigDecimal

data class CafeEntity(
    val id: Int,
    val name: String,
    val latitude: BigDecimal,
    val longitude: BigDecimal,
    val distanceMeters: Float? = null
)