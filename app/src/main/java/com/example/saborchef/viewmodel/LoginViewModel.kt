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

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val dataStoreManager = DataStoreManager(application)

    fun login(alias: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                AuthRepository.login(alias, password)
                    .onSuccess { authResponse ->
                        // Guarda la sesión completa en DataStore (sin el campo nombre)
                        dataStoreManager.saveUserData(
                            alias = authResponse.alias,
                            email = authResponse.email,
                            userId = authResponse.user_id,
                            role = authResponse.role.name,
                            token = authResponse.access_token
                        )

                        SessionManager.token = authResponse.access_token

                        _loginState.value = LoginState.Success(authResponse.access_token)
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
