package com.example.saborchef.network

import com.example.saborchef.model.ConfirmacionCodigoDTO
import com.example.saborchef.model.RegisterRequest
import com.example.saborchef.models.AuthenticationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Interfaz Retrofit que define los endpoints de autenticación y registro.
 * Fíjate en los paths: “auth/authenticate”, “auth/register” y “usuarios/confirmar-codigo”.
 */
interface AuthApiService {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("auth/authenticate")
    suspend fun login(@Body request: LoginRequest): Response<AuthenticationResponse>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthenticationResponse>

    @POST("usuarios/confirmar-codigo")
    suspend fun confirmarCuenta(@Body dto: ConfirmacionCodigoDTO): Response<Void>
}
