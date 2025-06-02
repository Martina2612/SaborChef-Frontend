// HomeViewModel.kt
package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.CalificacionControllerApi
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.models.TopRecetaResponse
import com.example.saborchef.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- Estados de UI para Home ---
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val role: UserRole,
        val topRecetas: List<TopRecetaResponse>,
        val ultimasRecetas: List<RecetaDetalleResponse>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val dataStore: DataStoreManager
) : ViewModel() {

    // --- Estado interno expuesto a la UI ---
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // --- Cliente de API lazy ---
    private val recetaService: RecetaControllerApi by lazy {
        ApiClient(baseUrl = BuildConfig.BASE_URL)
            .createService(RecetaControllerApi::class.java)
    }
    private val calificacionService: CalificacionControllerApi by lazy {
        ApiClient(baseUrl = BuildConfig.BASE_URL)
            .createService(CalificacionControllerApi::class.java)
    }

    init {
        fetchHomeData()
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // 1) Leemos el rol desde DataStore (primer valor)
                val roleString: String? = withContext(Dispatchers.IO) {
                    dataStore.role.first()
                }
                val role = roleString
                    ?.uppercase()
                    ?.let { UserRole.valueOf(it) }
                    ?: UserRole.VISITANTE

                // 2) Hacemos las llamadas de red en I/O
                val topCall = withContext(Dispatchers.IO) {
                    calificacionService.obtenerTopRecetas(12).execute()
                }
                val ultimasCall = withContext(Dispatchers.IO) {
                    recetaService.getUltimasRecetas().execute()
                }

                if (!topCall.isSuccessful) {
                    val err = topCall.errorBody()?.string() ?: "Error desconocido"
                    throw Exception("TopRecetas: ${topCall.code()} - $err")
                }
                if (!ultimasCall.isSuccessful) {
                    val err = ultimasCall.errorBody()?.string() ?: "Error desconocido"
                    throw Exception("ÚltimasRecetas: ${ultimasCall.code()} - $err")
                }

                val topList = topCall.body().orEmpty()
                val ultimasList = ultimasCall.body().orEmpty()

                // 3) Emitimos Success
                _uiState.value = HomeUiState.Success(
                    role = role,
                    topRecetas = topList,
                    ultimasRecetas = ultimasList
                )
                Log.d("HomeViewModel", "Home data cargada: top=${topList.size}, ultimas=${ultimasList.size}")

            } catch (e: Exception) {
                // Capturamos errores de red, parsing o DataStore
                Log.e("HomeViewModel", "Error al cargar Home", e)
                _uiState.value = HomeUiState.Error(
                    "No se pudo cargar la pantalla de inicio: ${e.message}"
                )
            }
        }
    }
}
