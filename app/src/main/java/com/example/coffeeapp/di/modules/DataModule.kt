package com.example.coffeeapp.di.modules

import com.example.coffeeapp.data.repositories.AuthRepositoryImpl
import com.example.coffeeapp.data.repositories.CafesRepositoryImpl
import com.example.coffeeapp.data.repositories.ProductsRepositoryImpl
import com.example.coffeeapp.data.repositories.TokenRepositoryImpl
import com.example.coffeeapp.domain.repositories.AuthRepository
import com.example.coffeeapp.domain.repositories.CafesRepository
import com.example.coffeeapp.domain.repositories.ProductsRepository
import com.example.coffeeapp.domain.repositories.TokenRepository
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @Binds
    fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindCafesRepository(repository: CafesRepositoryImpl): CafesRepository

    @Binds
    fun bindProductsRepository(repository: ProductsRepositoryImpl): ProductsRepository

    @Binds
    fun bindTokenRepository(repository: TokenRepositoryImpl): TokenRepository
}