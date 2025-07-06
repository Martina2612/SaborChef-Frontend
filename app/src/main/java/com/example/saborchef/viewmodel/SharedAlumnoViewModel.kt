package com.example.saborchef.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.saborchef.model.Rol
import com.example.saborchef.model.RegisterRequest
import android.util.Base64

class SharedAlumnoViewModel : ViewModel() {
    // Datos del usuario
    var alias: String = ""
        private set
    var email: String = ""
        private set
    var password: String = ""
        private set
    var rol: Rol = Rol.VISITANTE
        private set

    // Datos personales (opcional)
    var nombre: String = ""
        private set
    var apellido: String = ""
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
    var tipoTarjeta: String = ""
        private set

    // --- SETTERS ---
    fun setUserInfo(alias: String, email: String, password: String, rol: Rol) {
        this.alias = alias
        this.email = email
        this.password = password
        this.rol = rol
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setPersonalInfo(nombre: String, apellido: String) {
        this.nombre = nombre
        this.apellido = apellido
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

    // ✅ Función para resetear todos los datos
    fun reset() {
        alias = ""
        email = ""
        password = ""
        rol = Rol.VISITANTE
        nombre = ""
        apellido = ""
        frontUri = null
        backUri = null
        tramite = ""
        cardNumber = ""
        securityCode = ""
        expiryDate = ""
        tipoTarjeta = ""
    }

    private fun uriToBase64(context: Context, uri: Uri?): String? {
        return try {
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            null
        }
    }

    // ✅ Conversión final a DTO enviando null para DNI
    fun toRegisterRequest(context: Context): RegisterRequest {
        return RegisterRequest(
            // Solo enviar nombre y apellido si no están vacíos
            nombre = if (nombre.isNotBlank()) nombre else null,
            apellido = if (apellido.isNotBlank()) apellido else null,
            alias = alias,
            email = email,
            password = password,
            role = rol,
            numeroTarjeta = cardNumber.ifBlank { null },
            tipoTarjeta = tipoTarjeta.ifBlank { null },
            vencimiento = expiryDate.ifBlank { null },
            codigoSeguridad = securityCode.ifBlank { null },
            dniFrente = null, // No enviar nada
            dniDorso = null,  // No enviar nada
            numeroTramite = tramite.ifBlank { null }
        )
    }
}