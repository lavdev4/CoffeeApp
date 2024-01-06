package com.example.coffeeapp.di.modules

import com.example.coffeeapp.data.network.ApiService
import com.example.coffeeapp.data.network.interceptors.NetworkConnectionInterceptor
import com.example.coffeeapp.di.annotations.ApiBaseUrl
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal

@Module
interface NetworkModule {

    companion object {
        @ApplicationScope
        @Provides
        fun provideGsonBigDecimalTypeAdapter(): TypeAdapter<BigDecimal> {
            return object : TypeAdapter<BigDecimal>() {
                override fun write(writer: JsonWriter?, value: BigDecimal?) {
                    writer?.value(value.toString())
                }
                override fun read(reader: JsonReader?): BigDecimal {
                    return BigDecimal(reader?.nextString())
                }
            }
        }

        @ApplicationScope
        @Provides
        fun provideGson(
            bigDecimalTypeAdapter: TypeAdapter<BigDecimal>
        ): Gson {
            return GsonBuilder()
                .registerTypeAdapter(BigDecimal::class.java, bigDecimalTypeAdapter)
                .create()
        }

        @ApplicationScope
        @Provides
        fun providerOkHttpClient(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .build()
        }

        @ApplicationScope
        @Provides
        fun provideConverterFactory(gson: Gson): Converter.Factory {
            return GsonConverterFactory.create(gson)
        }

        @ApplicationScope
        @Provides
        fun provideRetrofit(
            @ApiBaseUrl apiBaseUrl: String,
            converterFactory: Converter.Factory,
            client: OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .addConverterFactory(converterFactory)
                .client(client)
                .baseUrl(apiBaseUrl)
                .build()
        }

        @ApplicationScope
        @Provides
        fun provideApiService(retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }
    }
}