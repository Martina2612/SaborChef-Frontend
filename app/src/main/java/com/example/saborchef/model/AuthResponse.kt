package com.example.saborchef.model

data class AuthResponse(
    val access_token: String,
    val user_id: Long,
    val role: Rol,
    val email: String
)
