package com.example.coffeeapp.data.network.interceptors

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.coffeeapp.di.annotations.ApplicationScope
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import javax.inject.Inject

@ApplicationScope
class NetworkConnectionInterceptor @Inject constructor(
    private val applicationContext: Context,
) : Interceptor {

    override fun intercept(chain: Chain): Response {
        if (!isConnected) return buildNoInternetResponse(chain)
        return chain.proceed(chain.request())
    }

    private fun buildNoInternetResponse(chain: Chain): Response {
        return Response.Builder()
            .protocol(Protocol.HTTP_2)
            .request(chain.request())
            .code(1)
            .message("No network connection")
            .body(ResponseBody.create(null, ""))
            .build()
    }

    private val isConnected: Boolean
        get() {
            val connectivityManager = applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }
}