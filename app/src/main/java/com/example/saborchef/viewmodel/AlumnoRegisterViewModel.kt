// AlumnoRegisterViewModel.kt
package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.Rol
import com.example.saborchef.model.RegisterRequest
import com.example.saborchef.models.AuthenticationResponse
import com.example.saborchef.network.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterAlumnoUiState {
    object Idle : RegisterAlumnoUiState()
    object Loading : RegisterAlumnoUiState()
    data class Success(val auth: AuthenticationResponse) : RegisterAlumnoUiState()
    data class Error(val message: String) : RegisterAlumnoUiState()
}

data class RegistroAlumnoData(
    val nombre: String = "",
    val apellido: String = "",
    val alias: String = "",
    val email: String = "",
    val password: String = "",
    val dniFrenteBase64: String = "",
    val dniDorsoBase64: String = "",
    val numeroTramite: String = "",
    val numeroTarjeta: String = "",
    val tipoTarjeta: String = "",
    val vencimiento: String = "",
    val codigoSeguridad: String = ""
)

class AlumnoRegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterAlumnoUiState>(RegisterAlumnoUiState.Idle)
    val uiState: StateFlow<RegisterAlumnoUiState> = _uiState

    var formData = MutableStateFlow(RegistroAlumnoData())

    fun actualizarDatosPersonales(nombre: String, apellido: String, alias: String, email: String, password: String) {
        formData.value = formData.value.copy(
            nombre = nombre,
            apellido = apellido,
            alias = alias,
            email = email,
            password = password
        )
    }

    fun actualizarDni(frente: String, dorso: String, tramite: String) {
        formData.value = formData.value.copy(
            dniFrenteBase64 = frente,
            dniDorsoBase64 = dorso,
            numeroTramite = tramite
        )
    }

    fun actualizarTarjeta(nro: String, tipo: String, venc: String, cvv: String) {
        formData.value = formData.value.copy(
            numeroTarjeta = nro,
            tipoTarjeta = tipo,
            vencimiento = venc,
            codigoSeguridad = cvv
        )
    }

    fun registrarAlumnoFinal() {
        _uiState.value = RegisterAlumnoUiState.Loading

        val datos = formData.value
        val request = RegisterRequest(
            nombre = datos.nombre,
            apellido = datos.apellido,
            alias = datos.alias,
            email = datos.email,
            password = datos.password,
            role = Rol.ALUMNO,
            dniFrente = datos.dniFrenteBase64,
            dniDorso = datos.dniDorsoBase64,
            numeroTramite = datos.numeroTramite,
            numeroTarjeta = datos.numeroTarjeta,
            tipoTarjeta = datos.tipoTarjeta,
            vencimiento = datos.vencimiento,
            codigoSeguridad = datos.codigoSeguridad
        )

        viewModelScope.launch {
            val result = AuthRepository.registerUser(request)
            result.onSuccess {
                _uiState.value = RegisterAlumnoUiState.Success(it)
            }.onFailure {
                _uiState.value = RegisterAlumnoUiState.Error(it.message ?: "Error inesperado")
            }
        }
    }
}

