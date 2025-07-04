package com.example.saborchef.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.ComentarioControllerApi
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.url
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.ComentarioRequest
import com.example.saborchef.models.ComentarioResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ComentariosViewModel(app: Application) : AndroidViewModel(app) {
    private val _comentarios = MutableStateFlow<List<ComentarioResponse>>(emptyList())
    val comentarios: StateFlow<List<ComentarioResponse>> = _comentarios

    private val api: ComentarioControllerApi by lazy {
        ApiClient
            .createAuthenticatedClient(url)
            .createService(ComentarioControllerApi::class.java)
    }
    private val dataStore = DataStoreManager(app)

    fun loadComentarios(idReceta: Long) {
        viewModelScope.launch {
            try {
                val resp: Response<List<ComentarioResponse>> = withContext(Dispatchers.IO) {
                    api.obtenerComentarios(idReceta).execute()
                }
                if (resp.isSuccessful) {
                    _comentarios.value = resp.body().orEmpty()
                } else Log.e("ComentariosVM","Error load: ${resp.code()}")
            } catch(e: Exception) {
                Log.e("ComentariosVM","Excepción load", e)
            }
        }
    }

    fun enviarComentario(idReceta: Long, texto: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val userId = dataStore.userId.first() ?: return@launch
                val request = ComentarioRequest(idReceta = idReceta, texto = texto)
                val resp: Response<ComentarioResponse> = withContext(Dispatchers.IO) {
                    api.crearComentario(request).execute()
                }
                if (resp.isSuccessful) {
                    onComplete()
                    loadComentarios(idReceta)
                } else Log.e("ComentariosVM","Error enviar: ${resp.code()}")
            } catch(e: Exception) {
                Log.e("ComentariosVM","Excepción enviar", e)
            }
        }
    }
}