package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.network.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Estados posibles para el login:
 *   - Idle: sin hacer nada
 *   - Loading: esperando respuesta
 *   - Success(token): login exitoso (token no vacío)
 *   - Error(message): hubo algún error (HTTP 401, 403, etc.)
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(alias: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val result = AuthRepository.login(alias, password)
                result.onSuccess { response ->
                    // Guardamos token y datos
                    dataStoreManager.saveLoginData(
                        token = response.access_token,
                        role = response.role.name,
                        email = response.email,
                        userId = response.user_id
                    )
                    _loginState.value = LoginState.Success(response.access_token)
                }.onFailure {
                    _loginState.value = LoginState.Error(it.localizedMessage ?: "Error desconocido")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }
}

