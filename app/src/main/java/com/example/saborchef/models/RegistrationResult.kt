package com.example.saborchef.models

data class RegistrationResult(
    val email: String,
    val userId: Long,
    val accessToken: String,
    val userRole: String
)
