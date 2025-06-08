package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.UsuarioControllerApi
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.AlumnoActualizarDTO
import com.example.saborchef.models.RegistrationResult
import com.example.saborchef.models.StudentRegistrationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

sealed class StudentUiState {
    object Idle    : StudentUiState()
    object Loading : StudentUiState()
    object Success : StudentUiState()
    data class Error(val message: String) : StudentUiState()
}

class StudentViewModel : ViewModel() {

    // Estado acumulado
    var data: StudentRegistrationData? = null
        private set

    // UI State de la llamada final
    private val _uiState = MutableStateFlow<StudentUiState>(StudentUiState.Idle)
    val uiState: StateFlow<StudentUiState> = _uiState

    private val api: UsuarioControllerApi =
        ApiClient().createService(UsuarioControllerApi::class.java)

    fun initWith(result: RegistrationResult) {
        data = StudentRegistrationData(
            email = result.email,
            userId = result.userId,
            accessToken = result.accessToken
        )
    }

    fun updatePayment(number: String, type: String, expiry: String, cvv: String) {
        data = data?.copy(
            cardNumber     = number,
            cardHolderName = type,
            expiryDate     = expiry,
            securityCode   = cvv
        )
    }

    /** Aquí guardamos **URLs** de las imágenes, no URIs */
    fun updateDniUrls(frontUrl: String, backUrl: String, tramite: String) {
        data = data?.copy(
            dniFrontUri   = frontUrl,
            dniBackUri    = backUrl,
            tramiteNumber = tramite
        )
    }

    /** Llama al endpoint para convertir en alumno */
    fun convertToStudent() {
        val d = data ?: return
        _uiState.value = StudentUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dto = AlumnoActualizarDTO(
                    numeroTarjeta   = d.cardNumber,
                    tipoTarjeta     = d.cardHolderName,
                    vencimiento     = d.expiryDate,
                    codigoSeguridad = d.securityCode,
                    dniFrente       = d.dniFrontUri.toString(),
                    dniDorso        = d.dniBackUri.toString(),
                    numeroTramite   = d.tramiteNumber
                )
                Log.d("DEBUG", "Llamando a API con userId=${data?.userId}")
                val resp = api.convertirEnAlumno(d.userId, dto).awaitResponse()
                Log.d("DEBUG", "Respuesta API: ${resp.code()}")
                if (resp.isSuccessful) {
                    _uiState.value = StudentUiState.Success
                } else {
                    _uiState.value = StudentUiState.Error("HTTP ${resp.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = StudentUiState.Error(e.localizedMessage ?: "Error de red")
                Log.e("DEBUG", "Error en convertToStudent", e)
            }
        }
    }
}
