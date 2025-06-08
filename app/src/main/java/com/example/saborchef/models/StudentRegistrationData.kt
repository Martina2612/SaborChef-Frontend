package com.example.saborchef.models

data class StudentRegistrationData(
    val email: String = "",
    val userId: Long = 0,
    val accessToken: String = "",

    val cardNumber: String = "",
    val securityCode: String = "",
    val expiryDate: String = "",
    val cardHolderName: String = "",

    val dniFrontUri: String = "",
    val dniBackUri: String = "",
    val tramiteNumber: String = ""
)
