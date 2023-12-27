package com.example.coffeeapp.di.modules

import com.example.coffeeapp.data.AuthRepositoryImpl
import com.example.coffeeapp.data.CafesRepositoryImpl
import com.example.coffeeapp.data.ProductsRepositoryImpl
import com.example.coffeeapp.domain.AuthRepository
import com.example.coffeeapp.domain.CafesRepository
import com.example.coffeeapp.domain.ProductsRepository
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
}