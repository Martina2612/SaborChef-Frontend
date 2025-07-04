package com.example.saborchef.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.FavoritaControllerApi
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.url
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.Receta
import com.example.saborchef.models.RecetaDetalleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class FavoritesViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)
    private val _favorites = MutableStateFlow<List<RecetaDetalleResponse>>(emptyList())
    val favorites: StateFlow<List<RecetaDetalleResponse>> = _favorites

    private val api: FavoritaControllerApi by lazy {
        ApiClient
            .createAuthenticatedClient(url)
            .createService(FavoritaControllerApi::class.java)
    }

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val userId = dataStoreManager.userId.first()
            Log.d("FavoritesVM", "Cargando favoritas para userId=$userId")
            try {
                val resp: Response<List<RecetaDetalleResponse>> = withContext(Dispatchers.IO) {
                    api.listar().execute()
                }
                Log.d("FavoritesVM", "loadFavorites HTTP ${resp.code()}, success=${resp.isSuccessful}")
                if (resp.isSuccessful) {
                    _favorites.value = resp.body() ?: emptyList()
                    Log.d("FavoritesVM", "Favorites cargadas: ${_favorites.value.size}")
                } else {
                    Log.e("FavoritesVM", "Error en loadFavorites: ${resp.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FavoritesVM", "Excepción en loadFavorites", e)
            }
        }
    }

    fun addFavorite(recetaId: Long?) {
        viewModelScope.launch {
            val userId = dataStoreManager.userId.first()
            Log.d("FavoritesVM", "Intentando agregar favorite: userId=$userId, recetaId=$recetaId")
            try {
                val resp = withContext(Dispatchers.IO) {
                    api.agregar(recetaId!!).execute()
                }
                Log.d("FavoritesVM", "addFavorite HTTP ${resp.code()}, success=${resp.isSuccessful}")
                if (!resp.isSuccessful) {
                    Log.e("FavoritesVM", "Error en addFavorite: ${resp.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FavoritesVM", "Excepción en addFavorite", e)
            }
            loadFavorites()
        }
    }

    fun removeFavorite(recetaId: Long?) {
        viewModelScope.launch {
            val userId = dataStoreManager.userId.first()
            Log.d("FavoritesVM", "Intentando remover favorite: userId=$userId, recetaId=$recetaId")
            try {
                val resp = withContext(Dispatchers.IO) {
                    api.eliminar1(recetaId!!).execute()
                }
                Log.d("FavoritesVM", "removeFavorite HTTP ${resp.code()}, success=${resp.isSuccessful}")
                if (!resp.isSuccessful) {
                    Log.e("FavoritesVM", "Error en removeFavorite: ${resp.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FavoritesVM", "Excepción en removeFavorite", e)
            }
            loadFavorites()
        }
    }
}
