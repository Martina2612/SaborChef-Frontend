package com.example.saborchef.models

data class PaymentMethod(
    val id: Long,
    val alias: String,
    val tipo: String, // "VISA", "MASTERCARD", etc.
    val numeroTarjeta: String? = null, // Los últimos 4 dígitos si los tienes
    val fechaCreacion: String? = null
)