package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update // ✅ necesario
import com.example.saborchef.viewmodel.PasswordRecoveryViewModel.PasswordRecoveryStep

data class PasswordRecoveryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentStep: PasswordRecoveryStep = PasswordRecoveryStep.EMAIL_INPUT,
    val email: String = "",
    val verificationCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isEmailSent: Boolean = false,
    val isCodeVerified: Boolean = false,
    val isPasswordReset: Boolean = false,
    val timeLeft: Int = 300,
    val canResendCode: Boolean = false
)

class PasswordRecoveryViewModel : ViewModel() {

    enum class PasswordRecoveryStep {
        EMAIL_INPUT,
        CODE_VERIFICATION,
        NEW_PASSWORD,
        SUCCESS
    }

    private val _uiState = MutableStateFlow(PasswordRecoveryUiState())
    val uiState: StateFlow<PasswordRecoveryUiState> = _uiState

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun sendPasswordResetEmail() {
        // Simular envío de email
        _uiState.update {
            it.copy(
                isEmailSent = true,
                currentStep = PasswordRecoveryStep.CODE_VERIFICATION
            )
        }
    }

    fun updateVerificationCode(code: String) {
        _uiState.update { it.copy(verificationCode = code) }
    }

    fun verifyCode() {
        // Simular verificación del código
        _uiState.update {
            it.copy(
                isCodeVerified = true,
                currentStep = PasswordRecoveryStep.NEW_PASSWORD
            )
        }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun resetPassword() {
        // Simular cambio de contraseña
        if (_uiState.value.newPassword == _uiState.value.confirmPassword) {
            _uiState.update {
                it.copy(
                    isPasswordReset = true,
                    currentStep = PasswordRecoveryStep.SUCCESS
                )
            }
        } else {
            _uiState.update {
                it.copy(error = "Las contraseñas no coinciden")
            }
        }
    }

    fun goBack() {
        when (_uiState.value.currentStep) {
            PasswordRecoveryStep.CODE_VERIFICATION -> _uiState.update { it.copy(currentStep = PasswordRecoveryStep.EMAIL_INPUT) }
            PasswordRecoveryStep.NEW_PASSWORD -> _uiState.update { it.copy(currentStep = PasswordRecoveryStep.CODE_VERIFICATION) }
            else -> {}
        }
    }

    fun resetState() {
        _uiState.value = PasswordRecoveryUiState()
    }
}

