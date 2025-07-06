package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.Cronograma
import com.example.saborchef.model.Curso
import com.example.saborchef.model.BajaCursoResponse // AGREGAR IMPORT
import com.example.saborchef.network.CronogramaRepository
import com.example.saborchef.network.CursoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

sealed class CursoUiState {
    object Loading : CursoUiState()
    data class Success(val cursos: List<Curso>) : CursoUiState()
    data class Error(val message: String) : CursoUiState()
}

// AGREGAR: Estados para el flujo de baja con reintegro
sealed class BajaUiState {
    object Idle : BajaUiState()
    object CalculandoReintegro : BajaUiState()
    data class ReintegroCalculado(val reintegroInfo: BajaCursoResponse) : BajaUiState()
    object EjecutandoBaja : BajaUiState()
    data class BajaExitosa(val mensaje: String) : BajaUiState()
    data class Error(val mensaje: String) : BajaUiState()
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

    // CAMBIAR: Estados para el nuevo flujo de baja
    private val _bajaUiState = MutableStateFlow<BajaUiState>(BajaUiState.Idle)
    val bajaUiState: StateFlow<BajaUiState> = _bajaUiState

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

    fun fetchCursos(idUsuario: Long) {
        viewModelScope.launch {
            try {
                val cursos = CursoRepository.getAllCursos(idUsuario)
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


    fun calcularReintegro(idCronograma: Long, idAlumno: Long) {
        _bajaUiState.value = BajaUiState.CalculandoReintegro
        viewModelScope.launch {
            try {
                val result = CursoRepository.calcularReintegro(idCronograma, idAlumno)
                result.fold(
                    onSuccess = { reintegroInfo ->
                        _bajaUiState.value = BajaUiState.ReintegroCalculado(reintegroInfo)
                    },
                    onFailure = { error ->
                        _bajaUiState.value = BajaUiState.Error("Error al calcular reintegro: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _bajaUiState.value = BajaUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }


    fun ejecutarBaja(idCronograma: Long, idAlumno: Long, token: String) {
        _bajaUiState.value = BajaUiState.EjecutandoBaja
        viewModelScope.launch {
            try {
                val response = CursoRepository.darseDeBaja(token, idCronograma, idAlumno)
                if (response.isSuccessful) {
                    val mensaje = response.body()?.mensaje ?: "Baja realizada exitosamente"
                    _bajaUiState.value = BajaUiState.BajaExitosa(mensaje)
                } else {
                    _bajaUiState.value = BajaUiState.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                _bajaUiState.value = BajaUiState.Error("Error de red: ${e.message}")
            }
        }
    }


    fun limpiarEstadoBaja() {
        _bajaUiState.value = BajaUiState.Idle
    }


    @Deprecated("Usar calcularReintegro() y ejecutarBaja() en su lugar")
    fun darseDeBaja(idCronograma: Long, idAlumno: Long, token: String) {
        ejecutarBaja(idCronograma, idAlumno, token)
    }

    @Deprecated("Usar bajaUiState en su lugar")
    private val _bajaExitosa = mutableStateOf<Boolean?>(null)
    val bajaExitosa: State<Boolean?> = _bajaExitosa
}



