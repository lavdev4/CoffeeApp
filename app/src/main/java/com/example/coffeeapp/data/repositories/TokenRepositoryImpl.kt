package com.example.coffeeapp.data.repositories

import android.content.SharedPreferences
import com.example.coffeeapp.di.annotations.ApplicationScope
import com.example.coffeeapp.domain.repositories.TokenRepository
import javax.inject.Inject

@ApplicationScope
class TokenRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : TokenRepository {

    companion object {
        private const val TOKEN_TAG = "token"
        private const val TOKEN_TYPE = "Bearer "
    }

    override fun setAuthToken(token: String) {
        sharedPreferences.edit()
            .putString(TOKEN_TAG, TOKEN_TYPE + token)
            .apply()
    }

    override fun getAuthToken(): String? {
        return sharedPreferences.getString(TOKEN_TAG, null)
    }
}