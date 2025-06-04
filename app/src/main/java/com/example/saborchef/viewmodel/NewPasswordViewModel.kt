package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewPasswordViewModel : ViewModel() {

    sealed class UpdateState {
        object Idle : UpdateState()
        object Loading : UpdateState()
        object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState

    fun updatePassword(email: String, code: String, newPassword: String) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading

            try {
                // Simulación de llamada de red o lógica real de actualización de contraseña
                delay(2000)

                // Aquí deberías reemplazar con tu lógica real de backend para validar el código y actualizar la contraseña
                val isCodeValid = code == "123456" // Validación ficticia
                if (isCodeValid) {
                    // Simulamos éxito
                    _updateState.value = UpdateState.Success
                } else {
                    _updateState.value = UpdateState.Error("Código de verificación incorrecto")
                }

            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("Error inesperado: ${e.message}")
            }
        }
    }
}
