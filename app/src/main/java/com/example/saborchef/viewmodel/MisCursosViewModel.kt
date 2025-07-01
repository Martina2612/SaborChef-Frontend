package com.example.saborchef.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.model.CursoInscripto
import com.example.saborchef.network.CronogramaApi
import com.example.saborchef.network.CronogramaRepository
import com.example.saborchef.data.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import kotlinx.coroutines.flow.first

class MisCursosViewModel(context: Context) : ViewModel() {

    private val dataStore = DataStoreManager(context)

    private val _cursos = MutableStateFlow<List<CursoInscripto>>(emptyList())
    val cursos: StateFlow<List<CursoInscripto>> = _cursos

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    init {
        viewModelScope.launch {
            val id = dataStore.userId.first()
            if (id != null) {
                try {
                    val cursosInscripto = CronogramaRepository.getCursosInscripto(id)
                    _cursos.value = cursosInscripto
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _loading.value = false
                }
            }
        }
    }

}
