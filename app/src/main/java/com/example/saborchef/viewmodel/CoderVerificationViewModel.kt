package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.CodeVerificationDto
import com.example.saborchef.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CodeVerificationViewModel : ViewModel() {

    sealed class VerificationState {
        object Idle : VerificationState()
        object Loading : VerificationState()
        object Success : VerificationState()
        data class Error(val message: String) : VerificationState()
    }

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState

    fun verifyCode(email: String, code: String) {
        _verificationState.value = VerificationState.Loading

        viewModelScope.launch {
            try {
                val response = RetrofitClient.usuarioApi.verifyRecoveryCode(
                    CodeVerificationDto(email = email, code = code)
                )
                if (response.isSuccessful && response.body() == true) {
                    _verificationState.value = VerificationState.Success
                } else {
                    _verificationState.value = VerificationState.Error("Código incorrecto o expirado")
                }
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
    }
}

