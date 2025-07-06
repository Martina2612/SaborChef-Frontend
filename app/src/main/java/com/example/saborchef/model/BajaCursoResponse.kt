package com.example.saborchef.model

data class BajaCursoResponse(
    val mensaje: String,
    val montoReintegro: Double,
    val porcentajeReintegro: Double,
    val precioOriginal: Double,
    val tipoReintegro: String
)