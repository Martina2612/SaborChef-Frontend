package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.Sede
import com.example.saborchef.network.SedeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SedeViewModel : ViewModel() {
    private val repository = SedeRepository()
    private val _sede = MutableStateFlow<Sede?>(null)
    val sede: StateFlow<Sede?> = _sede

    fun obtenerSedePorId(id: Long) {
        viewModelScope.launch {
            _sede.value = repository.getSede(id)
        }
    }
}
