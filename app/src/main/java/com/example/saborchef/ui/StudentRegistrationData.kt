package com.example.saborchef.ui

import android.net.Uri

data class StudentRegistrationData(
    val email: String,
    val userId: Long,
    var cardNumber: String = "",
    var cardType: String = "",
    var expiryDate: String = "",
    var securityCode: String = "",
    var dniFrontUri: Uri? = null,
    var dniBackUri: Uri? = null,
    var tramiteNumber: String = ""
)