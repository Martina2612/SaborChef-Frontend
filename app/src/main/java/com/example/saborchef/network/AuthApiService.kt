package com.example.saborchef.network

import com.example.saborchef.model.ConfirmacionCodigoDTO
import com.example.saborchef.model.RegisterRequest
import com.example.saborchef.models.AuthenticationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/authenticate")
    suspend fun login(@Body request: LoginRequest): Response<AuthenticationResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthenticationResponse>

    @POST("usuarios/confirmar-codigo")
    suspend fun confirmarCuenta(@Body dto: ConfirmacionCodigoDTO): Response<Void>

    @POST("usuarios/password/send-code")
    suspend fun sendPasswordResetEmail(@Body request: PasswordResetRequest): Response<PasswordResetResponse>

    @POST("usuarios/password/verify-code")
    suspend fun verifyResetCode(@Body request: VerifyCodeRequest): Response<VerifyCodeResponse>

    @POST("usuarios/password/reset")
    suspend fun resetPassword(@Body request: NewPasswordRequest): PasswordResetResponse
}
