package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.PerfilUsuarioDTO
import com.example.saborchef.network.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles para la pantalla de Mis Datos
 */
sealed class MisDatosState {
    object Loading : MisDatosState()
    data class Success(val perfil: PerfilUsuarioDTO) : MisDatosState()
    data class Error(val message: String) : MisDatosState()
}

/**
 * Estados para la actualización del perfil
 */
sealed class ActualizarPerfilState {
    object Idle : ActualizarPerfilState()
    object Loading : ActualizarPerfilState()
    object Success : ActualizarPerfilState()
    data class Error(val message: String) : ActualizarPerfilState()
}

class MisDatosViewModel : ViewModel() {

    private val _datosState = MutableStateFlow<MisDatosState>(MisDatosState.Loading)
    val datosState: StateFlow<MisDatosState> = _datosState

    private val _actualizarState = MutableStateFlow<ActualizarPerfilState>(ActualizarPerfilState.Idle)
    val actualizarState: StateFlow<ActualizarPerfilState> = _actualizarState

    /**
     * Carga los datos del perfil del usuario
     */
    fun cargarDatos(userId: Long) {
        _datosState.value = MisDatosState.Loading
        viewModelScope.launch {
            UsuarioRepository.obtenerPerfil(userId)
                .onSuccess { perfil ->
                    _datosState.value = MisDatosState.Success(perfil)
                }
                .onFailure { e ->
                    Log.e("MisDatosViewModel", "Error al cargar datos", e)
                    _datosState.value = MisDatosState.Error(
                        e.message ?: "Error al cargar los datos del perfil"
                    )
                }
        }
    }

    /**
     * Actualiza el perfil del usuario
     */
    fun actualizarPerfil(userId: Long, nombre: String, apellido: String, telefono: String) {
        _actualizarState.value = ActualizarPerfilState.Loading
        viewModelScope.launch {
            // Obtener datos actuales
            val datosActuales = (_datosState.value as? MisDatosState.Success)?.perfil
            if (datosActuales == null) {
                _actualizarState.value = ActualizarPerfilState.Error("No se pudieron obtener los datos actuales")
                return@launch
            }

            // Crear DTO con datos actualizados
            val perfilActualizado = datosActuales.copy(
                nombre = nombre.takeIf { it.isNotBlank() },
                apellido = apellido.takeIf { it.isNotBlank() },
                telefono = telefono.takeIf { it.isNotBlank() }
            )

            UsuarioRepository.actualizarPerfil(userId, perfilActualizado)
                .onSuccess { perfilNuevo ->
                    _actualizarState.value = ActualizarPerfilState.Success
                    _datosState.value = MisDatosState.Success(perfilNuevo)
                }
                .onFailure { e ->
                    Log.e("MisDatosViewModel", "Error al actualizar perfil", e)
                    _actualizarState.value = ActualizarPerfilState.Error(
                        e.message ?: "Error al actualizar el perfil"
                    )
                }
        }
    }

    /**
     * Resetea el estado de actualización
     */
    fun resetActualizarState() {
        _actualizarState.value = ActualizarPerfilState.Idle
    }
}