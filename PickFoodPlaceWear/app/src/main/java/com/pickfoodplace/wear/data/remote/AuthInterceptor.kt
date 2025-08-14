package com.pickfoodplace.wear.data.remote

import com.pickfoodplace.wear.BuildConfig
import com.pickfoodplace.wear.data.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenStorage: TokenStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        tokenStorage.getAccessToken()?.let { token ->
            builder.addHeader("Authorization", "Bearer $token")
        }

        val request = builder.build()
        return chain.proceed(request)
    }
}