// src/main/java/com/example/saborchef/network/UsuarioApi.kt
package com.example.saborchef.network

import com.example.saborchef.model.PasswordResetRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.saborchef.model.EmailDto
import com.example.saborchef.model.CodeVerificationDto
interface UsuarioApi {
    @POST("api/usuarios/password/reset")
    suspend fun resetPassword(@Body request: PasswordResetRequest): Response<Void>
    @POST("/api/usuarios/password/send-code")
    suspend fun sendRecoveryCode(@Body request: EmailDto): Response<Void>

    @POST("/api/usuarios/password/verify-code")
    suspend fun verifyRecoveryCode(@Body request: CodeVerificationDto): Response<Boolean>
}
