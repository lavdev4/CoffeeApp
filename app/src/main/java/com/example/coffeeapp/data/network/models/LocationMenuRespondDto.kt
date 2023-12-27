package com.example.coffeeapp.data.network.models

import com.google.gson.annotations.SerializedName

data class LocationMenuRespondDto(
    val id: Int,
    val name: String,
    @SerializedName("imageURL")
    val imageUrl: String,
    val price: Int
)
