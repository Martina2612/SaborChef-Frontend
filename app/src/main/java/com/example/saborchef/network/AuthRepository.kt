package com.example.saborchef.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes para las requests/responses
data class LoginRequest(
    val alias: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User? = null
)

data class User(
    val id: Long,
    val email: String,
    val name: String? = null
)

// Interface de la API
interface AuthApiService {
    @POST("auth/authenticate")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

object AuthRepository {
    private val api: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/") // Para emulador Android
            // .baseUrl("http://localhost:8080/api/") // Para dispositivo físico en la misma red
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    suspend fun login(alias: String, password: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(alias, password))
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}