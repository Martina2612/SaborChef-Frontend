package com.example.saborchef.model

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val role: UserRole) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
