package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.data.url
import com.example.saborchef.apis.AuthenticationControllerApi
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.AuthenticationRequest
import com.example.saborchef.models.AuthenticationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val response: AuthenticationResponse) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    private val api by lazy {
        ApiClient(baseUrl = url)
            .createService(AuthenticationControllerApi::class.java)
    }

    fun login(alias: String, password: String) {
        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            try {
                val request = AuthenticationRequest(alias = alias, password = password)
                val response: Response<AuthenticationResponse> = withContext(Dispatchers.IO) {
                    api.authenticate(request).execute()
                }

                if (response.isSuccessful) {
                    response.body()?.let {
                        _uiState.value = AuthUiState.Success(it)
                    } ?: run {
                        _uiState.value = AuthUiState.Error("Respuesta vacía del servidor.")
                    }
                } else {
                    _uiState.value = AuthUiState.Error("Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Excepción en login", e)
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }
}
