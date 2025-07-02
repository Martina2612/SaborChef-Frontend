package com.example.saborchef.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.Clase
import com.example.saborchef.network.ClaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClasesViewModel : ViewModel() {
    private val _clases = MutableStateFlow<List<Clase>>(emptyList())
    val clases: StateFlow<List<Clase>> = _clases

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading
    private val _asistencias = mutableStateMapOf<Long, Boolean>()
    val asistencias: Map<Long, Boolean> get() = _asistencias

    fun cargarClasesPorCronograma(context: Context, idCronograma: Long) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _clases.value = ClaseRepository.getClasesPorCronograma(context, idCronograma)
            } catch (e: Exception) {
                e.printStackTrace()
                _clases.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun verificarAsistenciaParaClase(context: Context, claseId: Long) {
        viewModelScope.launch {
            try {
                val asistio = ClaseRepository.verificarAsistencia(context, claseId)
                _asistencias[claseId] = asistio
            } catch (e: Exception) {
                _asistencias[claseId] = false // si hay error, asumimos que no asistió
            }
        }
    }
}


