package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.apis.RecetaControllerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

sealed class RecipeDetailUiState {
    object Loading : RecipeDetailUiState()
    data class Success(val recipe: RecetaDetalleResponse) : RecipeDetailUiState()
    data class Error(val message: String) : RecipeDetailUiState()
}

class RecipeDetailViewModel(
    private val recipeId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState

    private val recetaApi: RecetaControllerApi by lazy {
        ApiClient(baseUrl = "http://192.168.1.37:8080/")
            .createService(RecetaControllerApi::class.java)
    }

    init {
        fetchRecipeDetail()
    }

    fun fetchRecipeDetail() {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            try {
                val response: Response<RecetaDetalleResponse> = withContext(Dispatchers.IO) {
                    recetaApi.obtener(recipeId).execute()
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uiState.value = RecipeDetailUiState.Success(body)
                        Log.d("RecipeDetailVM", "Detalle cargado: $body")
                    } else {
                        _uiState.value = RecipeDetailUiState.Error("Respuesta vacía del servidor.")
                    }
                } else {
                    val err = response.errorBody()?.string() ?: "Error ${response.code()}"
                    _uiState.value = RecipeDetailUiState.Error("Servidor: $err")
                    Log.e("RecipeDetailVM", "Error servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = RecipeDetailUiState.Error("Error de red: ${e.localizedMessage}")
                Log.e("RecipeDetailVM", "Exception al cargar detalle", e)
            }
        }
    }
}