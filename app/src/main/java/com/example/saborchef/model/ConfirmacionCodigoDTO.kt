package com.example.saborchef.model

/**
 * Este DTO debe coincidir con lo que espera el endpoint "/api/auth/usuarios/confirmar-codigo" en tu backend.
 */
data class ConfirmacionCodigoDTO(
    val email: String,
    val codigo: String
)
