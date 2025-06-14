package com.example.saborchef.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.AuthResponse
import com.example.saborchef.model.ConfirmacionCodigoDTO
import com.example.saborchef.model.RegisterRequest
import com.example.saborchef.model.Rol
import com.example.saborchef.models.AuthenticationResponse
import com.example.saborchef.network.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

// Estados posibles de la UI de registro/confirmación.
sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val auth: AuthResponse) : RegisterUiState()
    data class SuccessUnit(val message: String) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

// Estados de validación de campos alias/email
sealed class FieldState {
    object Idle : FieldState()
    object Valid : FieldState()
    data class Taken(val suggestions: List<String> = emptyList()) : FieldState()
    data class Error(val message: String) : FieldState()
    object Loading : FieldState()
}

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val _aliasState = MutableStateFlow<FieldState>(FieldState.Idle)
    val aliasState: StateFlow<FieldState> = _aliasState
    private var aliasJob: Job? = null

    private val _emailState = MutableStateFlow<FieldState>(FieldState.Idle)
    val emailState: StateFlow<FieldState> = _emailState
    private var emailJob: Job? = null

    // ✅ Nuevo método que usa SharedAlumnoViewModel y context
    fun register(context: Context, sharedAlumnoViewModel: SharedAlumnoViewModel) {
        _uiState.value = RegisterUiState.Loading

        viewModelScope.launch {
            try {
                val request = sharedAlumnoViewModel.toRegisterRequest(context)
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

    // Confirmar código
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

    fun checkAlias(alias: String) {
        aliasJob?.cancel()
        _aliasState.value = FieldState.Idle
        if (alias.length < 3) return

        aliasJob = viewModelScope.launch {
            delay(400)
            try {
                val available = AuthRepository.isAliasAvailable(alias)
                if (available) {
                    _aliasState.value = FieldState.Valid
                } else {
                    val suggestions = List(3) { alias + (10..99).random() }
                    _aliasState.value = FieldState.Taken(suggestions)
                }
            } catch (e: HttpException) {
                _aliasState.value = FieldState.Error("Error del servidor: ${e.code()}")
            } catch (e: Exception) {
                _aliasState.value = FieldState.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun checkEmail(email: String) {
        emailJob?.cancel()
        _emailState.value = FieldState.Idle
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return

        emailJob = viewModelScope.launch {
            delay(400)
            try {
                val available = AuthRepository.isEmailAvailable(email)
                if (available) {
                    _emailState.value = FieldState.Valid
                } else {
                    _emailState.value = FieldState.Taken()
                }
            } catch (e: HttpException) {
                _emailState.value = FieldState.Error("Error del servidor: ${e.code()}")
            } catch (e: Exception) {
                _emailState.value = FieldState.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }
}
