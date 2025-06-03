package com.example.saborchef.network

/**
 * Representa el cuerpo (JSON) que se envía al endpoint `/auth/authenticate`.
 */
data class LoginRequest(
    val alias: String,
    val password: String
)
