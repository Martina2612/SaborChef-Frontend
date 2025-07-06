package com.example.saborchef.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.Clase
import com.example.saborchef.network.ClaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClasesViewModel : ViewModel() {

    private val _clases = MutableStateFlow<List<Clase>>(emptyList())
    val clases: StateFlow<List<Clase>> = _clases


    val asistencias = mutableStateMapOf<Long, Boolean>()


    fun cargarClasesPorCronograma(context: Context, idCronograma: Long) {
        viewModelScope.launch {
            try {
                val dataStore = DataStoreManager(context)
                val token = dataStore.token.first() ?: ""

                if (token.isNotBlank()) {
                    val clasesResult = ClaseRepository.getClasesPorCronograma(token, idCronograma)
                    _clases.value = clasesResult
                }
            } catch (e: Exception) {
                // Manejar error
                _clases.value = emptyList()
            }
        }
    }


    fun verificarAsistenciaParaClase(context: Context, claseId: Long) {
        viewModelScope.launch {
            try {
                val dataStore = DataStoreManager(context)
                val token = dataStore.token.first() ?: ""
                val userId = dataStore.userId.first() ?: 0L

                if (token.isNotBlank() && userId > 0) {
                    val tieneAsistencia = ClaseRepository.verificarAsistencia(token, claseId, userId)
                    asistencias[claseId] = tieneAsistencia
                }
            } catch (e: Exception) {
                // En caso de error, asumir que no tiene asistencia
                asistencias[claseId] = false
            }
        }
    }


    fun registrarAsistenciaConQR(
        context: Context,
        claseId: Long,
        qrCode: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val dataStore = DataStoreManager(context)
                val token = dataStore.token.first() ?: ""
                val userId = dataStore.userId.first() ?: 0L

                if (token.isBlank()) {
                    onResult(false, "No hay sesión activa")
                    return@launch
                }

                if (userId <= 0) {
                    onResult(false, "Usuario no válido")
                    return@launch
                }

                // Validar QR
                if (!validarQrCode(qrCode, claseId)) {
                    onResult(false, "Código QR inválido para esta clase")
                    return@launch
                }

                // Registrar asistencia
                val response = ClaseRepository.registrarAsistencia(token, claseId, userId)

                if (response.isSuccessful) {
                    // Actualizar estado local
                    asistencias[claseId] = true
                    onResult(true, "¡Asistencia registrada exitosamente!")
                } else {
                    onResult(false, "Error al registrar asistencia: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult(false, "Error de conexión: ${e.message}")
            }
        }
    }


    private fun validarQrCode(qrCode: String, claseId: Long): Boolean {
        // Para demo - cualquier QR que no esté vacío es válido
        return qrCode.trim().isNotEmpty()
    }
}


