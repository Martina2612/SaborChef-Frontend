package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.Curso
import com.example.saborchef.network.CursoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CursoUiState {
    object Loading : CursoUiState()
    data class Success(val cursos: List<Curso>) : CursoUiState()
    data class Error(val message: String) : CursoUiState()
}

class CursoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<CursoUiState>(CursoUiState.Loading)
    val uiState: StateFlow<CursoUiState> = _uiState

    init {
        fetchCursos()
    }

    private fun fetchCursos() {
        viewModelScope.launch {
            try {
                val cursos = CursoRepository.getAllCursos()
                _uiState.value = CursoUiState.Success(cursos)
            } catch (e: Exception) {
                _uiState.value = CursoUiState.Error("Error al obtener cursos: ${e.message}")
            }
        }
    }
}
