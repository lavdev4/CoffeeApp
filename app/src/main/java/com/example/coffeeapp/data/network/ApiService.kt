package com.example.coffeeapp.data.network

import com.example.coffeeapp.data.network.models.AuthResponseDto
import com.example.coffeeapp.data.network.models.LocationMenuRespondDto
import com.example.coffeeapp.data.network.models.LocationRespondDto
import com.example.coffeeapp.data.network.models.LoginDataDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body data: LoginDataDto): Response<AuthResponseDto>

    @POST("auth/login")
    suspend fun login(@Body data: LoginDataDto): Response<AuthResponseDto>

    @GET("locations")
    suspend fun getLocations(
        @Header("Authorization") token: String
    ): Response<List<LocationRespondDto>>

    @GET("location/{id}/menu")
    suspend fun getLocationsMenu(
        @Path("id") locationId: String,
        @Header("Authorization") token: String
    ): Response<List<LocationMenuRespondDto>>
}