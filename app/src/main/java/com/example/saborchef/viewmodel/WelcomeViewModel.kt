package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.RecetaDetalleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeViewModel : ViewModel() {

    // --- Estado de la UI ---
    // Usar Any para representar los diferentes estados: Loading, Success, Error.
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    // --- Configuración del Cliente de API ---
    // Es mejor instanciarlo de forma "lazy" para que solo se cree cuando se necesite.
    private val recetasService: RecetaControllerApi by lazy {
        val apiClient = ApiClient(baseUrl = "http://192.168.1.37:8080/")
        apiClient.createService(RecetaControllerApi::class.java)
    }

    init {
        fetchLatestRecipes()
    }

    fun fetchLatestRecipes() {
        // Inicia la corutina en el scope del ViewModel.
        viewModelScope.launch {
            // Informamos a la UI que estamos cargando datos.
            _uiState.value = UiState.Loading
            try {
                // *** LA CORRECCIÓN CLAVE ESTÁ AQUÍ ***
                // Movemos la llamada de red a un hilo de fondo (I/O).
                val response = withContext(Dispatchers.IO) {
                    // .execute() es seguro aquí porque ya no está en el hilo principal.
                    recetasService.obtenerUltimas3Recetas().execute()
                }

                if (response.isSuccessful) {
                    val recipes = response.body()
                    if (recipes.isNullOrEmpty()) {
                        // La llamada fue exitosa pero no trajo datos.
                        _uiState.value = UiState.Error("No se encontraron recetas.")
                        Log.d("WelcomeViewModel", "Respuesta exitosa pero sin recetas.")
                    } else {
                        // ¡Éxito! Enviamos los datos a la UI.
                        _uiState.value = UiState.Success(recipes)
                        Log.d("WelcomeViewModel", "Recetas cargadas exitosamente: ${recipes.size} recetas.")
                    }
                } else {
                    // La llamada falló por un error del servidor (ej. 404, 500).
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _uiState.value = UiState.Error("Error del servidor: ${response.code()} - $errorBody")
                    Log.e("WelcomeViewModel", "Error en la respuesta del servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                // La llamada falló por un problema de red o de otro tipo (ej. sin internet).
                Log.e("WelcomeViewModel", "Excepción al obtener recetas", e)
                _uiState.value = UiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}

// --- Clase sellada para manejar los estados de la UI de forma limpia ---
sealed class UiState {
    object Loading : UiState()
    data class Success(val recipes: List<RecetaDetalleResponse>) : UiState()
    data class Error(val message: String) : UiState()
}