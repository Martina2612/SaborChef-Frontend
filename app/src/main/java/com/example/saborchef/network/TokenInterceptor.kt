package com.example.saborchef.network

import com.example.saborchef.data.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder().apply {
            SessionManager.token?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }.build()
        return chain.proceed(req)
    }
}
