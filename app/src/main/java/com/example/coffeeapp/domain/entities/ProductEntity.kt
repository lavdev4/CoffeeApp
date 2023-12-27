package com.example.coffeeapp.domain.entities

data class ProductEntity(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val price: Int,
    val count: Int = 0
)