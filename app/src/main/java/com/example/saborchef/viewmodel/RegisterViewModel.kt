package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.ConfirmacionCodigoDTO
import com.example.saborchef.model.RegisterRequest
import com.example.saborchef.model.Rol
import com.example.saborchef.models.AuthenticationResponse
import com.example.saborchef.network.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Estados posibles de la UI de registro/confirmación.
sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val auth: AuthenticationResponse) : RegisterUiState()
    data class SuccessUnit(val message: String) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    /**
     *  Llama a AuthRepository.registerUser(...)
     */
    fun register(
        nombre: String,
        apellido: String,
        alias: String,
        email: String,
        password: String,
        role: Rol
    ) {
        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    nombre = nombre,
                    apellido = apellido,
                    alias = alias,
                    email = email,
                    password = password,
                    role = role
                )

                val result = withContext(Dispatchers.IO) {
                    AuthRepository.registerUser(request)
                }

                result.onSuccess { auth ->
                    _uiState.value = RegisterUiState.Success(auth)
                }.onFailure { e ->
                    Log.e("RegisterVM", "Error en registro: ${e.localizedMessage}", e)
                    _uiState.value = RegisterUiState.Error(e.localizedMessage ?: "Error inesperado")
                }

            } catch (e: Exception) {
                Log.e("RegisterVM", "Excepción al registrar", e)
                _uiState.value = RegisterUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }

    /**
     *  Llama a AuthRepository.confirmarCuenta(...)
     */
    fun confirmarCuenta(email: String, codigo: String) {
        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            try {
                val dto = ConfirmacionCodigoDTO(email = email, codigo = codigo)

                val result = withContext(Dispatchers.IO) {
                    AuthRepository.confirmarCuenta(dto)
                }

                result.onSuccess {
                    _uiState.value = RegisterUiState.SuccessUnit("Cuenta confirmada correctamente")
                }.onFailure { e ->
                    _uiState.value = RegisterUiState.Error(e.localizedMessage ?: "Error al confirmar")
                }

            } catch (e: Exception) {
                Log.e("RegisterVM", "Excepción al confirmar", e)
                _uiState.value = RegisterUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }
}
