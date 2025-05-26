package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.models.RegisterRequest
import com.example.saborchef.models.AuthenticationResponse
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.apis.AuthenticationControllerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val auth: AuthenticationResponse) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    private val api by lazy {
        ApiClient("http://192.168.1.37:8080/")
            .createService(AuthenticationControllerApi::class.java)
    }

    fun register(
        nombre: String,
        apellido: String,
        alias: String,
        email: String,
        password: String,
        role: RegisterRequest.Role
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
                val response: Response<AuthenticationResponse> = withContext(Dispatchers.IO) {
                    api.register(request).execute()
                }
                if (response.isSuccessful) {
                    response.body()?.let {
                        _uiState.value = RegisterUiState.Success(it)
                    } ?: run {
                        _uiState.value = RegisterUiState.Error("Respuesta vacía del servidor.")
                    }
                } else {
                    _uiState.value = RegisterUiState.Error("Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("RegisterVM", "Excepción registro", e)
                _uiState.value = RegisterUiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }
}