// src/main/java/com/example/saborchef/model/PasswordResetRequest.kt
package com.example.saborchef.model

data class PasswordResetRequest(
    val email: String,
    val newPassword: String
)
