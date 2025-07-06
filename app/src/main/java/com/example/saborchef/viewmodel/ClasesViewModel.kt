package com.example.saborchef.viewmodel

import android.content.Context
import android.util.Log
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
import java.time.LocalDate

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

                Log.d("DEBUG_ASISTENCIA", "=== REGISTRAR ASISTENCIA ===")
                Log.d("DEBUG_ASISTENCIA", "ClaseId: $claseId")
                Log.d("DEBUG_ASISTENCIA", "QR Code: '$qrCode'")

                if (token.isBlank()) {
                    onResult(false, "No hay sesión activa")
                    return@launch
                }

                if (userId <= 0) {
                    onResult(false, "Usuario no válido")
                    return@launch
                }

                // VALIDACIÓN MEJORADA con mensaje específico
                if (!validarQrCode(qrCode, claseId)) {
                    onResult(false, "❌ QR incorrecto\n\nEste código no corresponde a la Clase $claseId.\n\nVerifica que estés escaneando el QR correcto para esta clase.")
                    return@launch
                }

                // Registrar asistencia
                val response = ClaseRepository.registrarAsistencia(token, claseId, userId)

                if (response.isSuccessful) {
                    asistencias[claseId] = true
                    onResult(true, "✅ ¡Asistencia registrada!\n\nTu asistencia para la Clase $claseId ha sido registrada exitosamente.")
                } else {
                    val mensaje = when (response.code()) {
                        403 -> "Sin permisos para registrar asistencia"
                        401 -> "Sesión expirada"
                        404 -> "Clase no encontrada"
                        409 -> "Asistencia ya registrada"
                        else -> "Error del servidor: ${response.code()}"
                    }
                    onResult(false, mensaje)
                }
            } catch (e: Exception) {
                onResult(false, "Error de conexión: ${e.message}")
            }
        }
    }


    private fun validarQrCode(qrCode: String, claseId: Long): Boolean {
        Log.d("QR_VALIDATION", "Validando QR: '$qrCode' para clase: $claseId")

        // Lista de formatos válidos que acepta el sistema
        val formatosValidos = listOf(
            // Formato simple: solo el ID
            claseId.toString(),

            // Formato con prefijo
            "CLASE_$claseId",
            "CLASS_$claseId",
            "C$claseId",

            // Formato con fecha
            "CLASE_${claseId}_${LocalDate.now()}",

            // Formato JSON simple
            "{\"claseId\":$claseId}",
            "{\"clase\":$claseId}",

            // Para testing - códigos específicos por clase
            "COCINA_ITALIANA_CLASE_$claseId",
            "AULA_A_CLASE_$claseId"
        )

        // Verificar si el QR coincide con algún formato válido
        val esValido = formatosValidos.any { formato ->
            qrCode.trim().equals(formato, ignoreCase = true)
        }

        // También permitir QRs que contengan el ID de la clase
        val contieneId = qrCode.contains(claseId.toString())

        val resultado = esValido || contieneId

        Log.d("QR_VALIDATION", "Resultado validación: $resultado")
        Log.d("QR_VALIDATION", "Formatos probados: $formatosValidos")

        return resultado
    }
}


