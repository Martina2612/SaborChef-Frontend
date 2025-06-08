package com.example.saborchef.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.saborchef.model.Rol
import com.example.saborchef.model.RegisterRequest
import android.util.Base64

class SharedAlumnoViewModel : ViewModel() {
    // Datos del usuario
    var nombre: String = ""
        private set
    var apellido: String = ""
        private set
    var alias: String = ""
        private set
    var email: String = ""
        private set
    var password: String = ""
        private set
    var rol: Rol = Rol.VISITANTE
        private set

    // Datos de DNI
    var frontUri: Uri? = null
        private set
    var backUri: Uri? = null
        private set
    var tramite: String = ""
        private set

    // Datos de tarjeta
    var cardNumber: String = ""
        private set
    var securityCode: String = ""
        private set
    var expiryDate: String = ""
        private set
    var cardHolderName: String = ""
        private set
    var tipoTarjeta: String = ""
        private set


    // --- SETTERS ---
    fun setUserInfo(nombre: String, apellido: String, alias: String, email: String, password: String, rol: Rol) {
        this.nombre = nombre
        this.apellido = apellido
        this.alias = alias
        this.email = email
        this.password = password
        this.rol = rol
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setDniInfo(front: Uri?, back: Uri?, tramite: String) {
        this.frontUri = front
        this.backUri = back
        this.tramite = tramite
    }

    fun setCardInfo(number: String, code: String, expiry: String, tipo: String) {
        this.cardNumber = number
        this.securityCode = code
        this.expiryDate = expiry
        this.tipoTarjeta = tipo
    }




    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    // Conversión final a DTO
    fun toRegisterRequest(context: Context): RegisterRequest {
        return RegisterRequest(
            nombre = nombre,
            apellido = apellido,
            alias = alias,
            email = email,
            password = password,
            role = rol,
            dniFrente = frontUri?.let { uriToBase64(context, it) } ?: "",
            dniDorso = backUri?.let { uriToBase64(context, it) } ?: "",
            numeroTramite = tramite,
            numeroTarjeta = cardNumber,
            codigoSeguridad = securityCode,
            vencimiento = expiryDate,
            tipoTarjeta = tipoTarjeta
        )
    }


}

