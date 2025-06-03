package com.example.saborchef.models

import com.google.gson.annotations.SerializedName

/**
 * Este modelo refleja el JSON que envía tu backend cuando:
 *   - Registra (POST /api/auth/register)
 *   - Autentica (POST /api/auth/authenticate)
 *
 * Ejemplo de respuesta en Postman:
 * {
 *   "access_token": "eyJhbGci…",
 *   "user_id": 5,
 *   "role": "USUARIO",
 *   "email": "juan@correo.com"
 * }
 */
data class AuthenticationResponse(

    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("user_id")
    val userId: Long? = null,

    @SerializedName("role")
    val role: String? = null,

    @SerializedName("email")
    val email: String? = null
)
