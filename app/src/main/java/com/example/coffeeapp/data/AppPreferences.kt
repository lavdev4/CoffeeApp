package com.example.coffeeapp.data

import android.content.SharedPreferences
import com.example.coffeeapp.di.annotations.ApplicationScope
import javax.inject.Inject

@ApplicationScope
class AppPreferences @Inject constructor(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val TOKEN_TAG = "token"
        private const val TOKEN_TYPE = "Bearer "
    }

    fun setAuthToken(token: String) {
        sharedPreferences.edit()
            .putString(TOKEN_TAG, TOKEN_TYPE + token)
            .apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(TOKEN_TAG, null)
    }
}