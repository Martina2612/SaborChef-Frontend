package com.example.saborchef.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.CalificacionControllerApi
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.url
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.CalificacionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class CalificacionViewModel(private val recetaId: Long,app: Application) : AndroidViewModel(app) {

    private val _userRating = MutableStateFlow(0)
    val userRating: StateFlow<Int> = _userRating
    private val dataStore = DataStoreManager(app)
    private val _promedio = MutableStateFlow<Double?>(null)
    val promedio: StateFlow<Double?> = _promedio

    private val api: CalificacionControllerApi by lazy {
        ApiClient
            .createAuthenticatedClient(url)
            .createService(CalificacionControllerApi::class.java)
    }

    init {
        loadPromedio(recetaId)
        loadUserRating(recetaId)
    }

    fun loadPromedio(recetaId: Long) {
        viewModelScope.launch {
            try {
                val resp: Response<Double> = withContext(Dispatchers.IO) {
                    api.obtenerPromedio(recetaId).execute()
                }
                if (resp.isSuccessful) {
                    _promedio.value = resp.body()
                } else {
                    Log.e("CalifVM", "Error loadPromedio: ${resp.code()}")
                }
            } catch (e: Exception) {
                Log.e("CalifVM", "Excepción loadPromedio", e)
            }
        }
    }

    fun loadUserRating(recetaId: Long) {
        viewModelScope.launch {
            try {
                val resp = withContext(Dispatchers.IO) { api.obtenerMiCalificacion(recetaId).execute() }
                if (resp.isSuccessful) {
                    val rating = resp.body() ?: 0
                    Log.d("CalifVM", "Usuario ya calificó con: $rating")
                    _userRating.value = rating
                } else {
                    Log.e("CalifVM", "Err userR ${resp.code()}")
                }
            } catch(e: Exception){
                Log.e("CalifVM","Exc userR",e)
            }
        }
    }

    fun calificar(recetaId: Long, puntuacion: Int) {
        viewModelScope.launch {
            try {
                val userId = dataStore.userId.first() ?: return@launch
                val request = CalificacionRequest(idUsuario= userId, idReceta = recetaId, calificacion = puntuacion)
                val resp = withContext(Dispatchers.IO) {
                    api.calificar(request).execute()
                }
                if (resp.isSuccessful) {
                    // recarga el promedio
                    _userRating.value = puntuacion
                    loadPromedio(recetaId)
                } else {
                    Log.e("CalifVM", "Error calificar: ${resp.code()}")
                }
            } catch (e: Exception) {
                Log.e("CalifVM", "Excepción calificar", e)
            }
        }
    }
}
