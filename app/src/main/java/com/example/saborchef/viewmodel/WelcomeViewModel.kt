package com.example.saborchef.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {
    private val _recetas = MutableStateFlow<List<RecetaDetalleResponse>>(emptyList())
    val recetas: StateFlow<List<RecetaDetalleResponse>> = _recetas
    val cliente= ApiClient(baseUrl = "http://192.168.1.37:8080/")
    val recetasService=cliente.createService(RecetaControllerApi::class.java)
    init {
        viewModelScope.launch {
            try {
                val call = recetasService.obtenerUltimas3Recetas()
                val response = call.execute() // Ejecuta el Call
                if (response.isSuccessful) {
                    _recetas.value = response.body() ?: emptyList()
                } else {
                    // manejar error HTTP
                }
            } catch (e: Exception) {
                // manejar error de red
            }
        }
    }

}
