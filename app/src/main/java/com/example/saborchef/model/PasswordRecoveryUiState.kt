package com.example.saborchef.model

data class PasswordRecoveryUiState(
    val email: String = "",
    val code: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentStep: PasswordRecoveryStep = PasswordRecoveryStep.EMAIL_INPUT
)
