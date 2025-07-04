package com.example.saborchef.infrastructure

import android.util.Log
import com.example.saborchef.data.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = SessionManager.token
        val requestBuilder = original.newBuilder()
        if (!token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "Añadiendo token al header: Bearer $token")
            requestBuilder.addHeader("Authorization", "Bearer $token")
        } else {
            Log.w("AuthInterceptor", "Token vacío, no añadiendo header Authorization")
        }
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
