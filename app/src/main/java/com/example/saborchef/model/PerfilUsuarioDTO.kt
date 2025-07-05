package com.example.saborchef.model

data class PerfilUsuarioDTO(
    val id: Long,
    val nombre: String?,
    val apellido: String?,
    val alias: String,
    val email: String,
    val telefono: String?,
    val fotoPerfil: String?
)