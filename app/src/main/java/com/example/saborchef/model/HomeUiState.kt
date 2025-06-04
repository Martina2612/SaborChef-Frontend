package com.example.saborchef.model

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val role: Rol) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
