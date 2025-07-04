package com.example.saborchef.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.SessionManager
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

/**
 * ViewModel para manejo de login que guarda datos en DataStore.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val dataStoreManager = DataStoreManager(application)

    /**
     * Ejecuta login y almacena token, rol, userId y email en DataStore
     */
    fun login(alias: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                AuthRepository.login(alias, password)
                    .onSuccess { authResponse ->
                        // Guarda la sesión completa en DataStore
                        dataStoreManager.saveUserData(
                            token = authResponse.accessToken.toString(),
                            role = authResponse.role.toString(),
                            userId = authResponse.userId,
                            email = authResponse.email.toString()
                        )

                        // Actualiza el SessionManager con el token
                        SessionManager.token = authResponse.accessToken

                        _loginState.value = LoginState.Success(authResponse.accessToken.toString())
                    }
                    .onFailure { e ->
                        Log.e("LoginViewModel", "Login fallido", e)
                        _loginState.value = LoginState.Error(e.localizedMessage ?: "Error desconocido")
                    }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error inesperado en login", e)
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }
}

