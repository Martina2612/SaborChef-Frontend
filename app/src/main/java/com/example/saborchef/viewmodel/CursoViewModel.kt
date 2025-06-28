package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.Cronograma
import com.example.saborchef.model.Curso
import com.example.saborchef.network.CronogramaRepository
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

    private val _cursoDetalle = MutableStateFlow<Curso?>(null)
    val cursoDetalle: StateFlow<Curso?> = _cursoDetalle

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    private val _inscripcionExitosa = MutableStateFlow<Boolean?>(null)
    val inscripcionExitosa: StateFlow<Boolean?> = _inscripcionExitosa
    private val _cronogramaDetalle = MutableStateFlow<Cronograma?>(null)
    val cronogramaDetalle: StateFlow<Cronograma?> = _cronogramaDetalle

    fun getCursoPorId(id: Long) {
        viewModelScope.launch {
            try {
                val curso = CursoRepository.getCursoPorId(id)
                _cursoDetalle.value = curso
            } catch (e: Exception) {
                _cursoDetalle.value = null
            }
        }
    }

    fun inscribirse(idCronograma: Long, idAlumno: Long, token: String) {
        viewModelScope.launch {
            try {
                val response = CursoRepository.inscribirseACurso(token, idCronograma, idAlumno)
                if (response.isSuccessful) {
                    _inscripcionExitosa.value = true
                    _mensajeError.value = null
                } else {
                    _inscripcionExitosa.value = false
                    _mensajeError.value = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                _inscripcionExitosa.value = false
                _mensajeError.value = "Error de red: ${e.message}"
            }
        }
    }

    fun limpiarEstadoInscripcion() {
        _inscripcionExitosa.value = null
        _mensajeError.value = null
    }

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

    suspend fun obtenerCursoPorId(id: Long): Curso {
        return CursoRepository.getCursoPorId(id)
    }
    fun cargarCurso(curso: Curso) {
        _cursoDetalle.value = curso
    }

    fun getCronogramaPorId(id: Long) {
        viewModelScope.launch {
            try {
                val cronograma = CronogramaRepository.getCronogramaPorId(id)
                _cronogramaDetalle.value = cronograma
            } catch (e: Exception) {
                _cronogramaDetalle.value = null
            }
        }
    }

}



