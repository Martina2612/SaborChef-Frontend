package com.example.saborchef.model

data class RegisterRequest(

    val nombre: String? = null, // Ahora es opcional
    val apellido: String? = null, // Ahora es opcional
    val alias: String,
    val email: String,
    val password: String,
    val role: Rol,
    val dniFrente: String? = null,
    val dniDorso: String? = null,
    val numeroTramite: String? = null,
    val numeroTarjeta: String? = null,
    val codigoSeguridad: String? = null,
    val vencimiento: String? = null,
    val tipoTarjeta: String? = null
)