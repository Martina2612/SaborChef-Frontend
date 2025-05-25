package com.example.saborchef.model


data class RecetaDetalleResponse(
    val idReceta: Long,
    val nombreReceta: String,
    val descripcionReceta: String,
    val fotoPrincipal: String,
    // …añade aquí los demás campos que exponga tu DTO
)
