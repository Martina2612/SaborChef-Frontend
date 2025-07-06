package com.example.saborchef.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaEscaladoControllerApi
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.url
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.RecetaEscaladaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ScaledRecipeUiState {
    object Idle : ScaledRecipeUiState()
    object Loading : ScaledRecipeUiState()
    data class Success(val scaledRecipe: RecetaEscaladaResponse) : ScaledRecipeUiState()
    data class Error(val message: String) : ScaledRecipeUiState()
}

class ScaledRecipesViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)

    // Estado para la receta escalada actual (vista previa)
    private val _scaledRecipeState = MutableStateFlow<ScaledRecipeUiState>(ScaledRecipeUiState.Idle)
    val scaledRecipeState: StateFlow<ScaledRecipeUiState> = _scaledRecipeState.asStateFlow()

    // Lista de recetas guardadas del usuario
    private val _savedRecipes = MutableStateFlow<List<RecetaEscaladaResponse>>(emptyList())
    val savedRecipes: StateFlow<List<RecetaEscaladaResponse>> = _savedRecipes.asStateFlow()

    // Estado de guardado
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    // Contador de recetas guardadas
    private val _savedCount = MutableStateFlow(0)
    val savedCount: StateFlow<Int> = _savedCount.asStateFlow()

    private val api: RecetaEscaladoControllerApi by lazy {
        ApiClient
            .createAuthenticatedClient(url)
            .createService(RecetaEscaladoControllerApi::class.java)
    }

    init {
        loadSavedRecipes()
    }

    /**
     * Escala una receta por porciones deseadas
     */
    fun scaleRecipeByPortions(recipeId: Long, desiredPortions: Int) {
        viewModelScope.launch {
            _scaledRecipeState.value = ScaledRecipeUiState.Loading

            try {
                val response = withContext(Dispatchers.IO) {
                    api.escalarPorPorciones(recipeId, desiredPortions).execute()
                }

                if (response.isSuccessful) {
                    response.body()?.let { scaledRecipe ->
                        _scaledRecipeState.value = ScaledRecipeUiState.Success(scaledRecipe)
                        Log.d("ScaledRecipesVM", "Receta escalada exitosamente: ${scaledRecipe.porcionesEscaladas} porciones")
                    } ?: run {
                        _scaledRecipeState.value = ScaledRecipeUiState.Error("Respuesta vacía del servidor")
                    }
                } else {
                    val error = response.errorBody()?.string() ?: "Error desconocido"
                    _scaledRecipeState.value = ScaledRecipeUiState.Error(error)
                    Log.e("ScaledRecipesVM", "Error escalando receta: ${response.code()} - $error")
                }
            } catch (e: Exception) {
                _scaledRecipeState.value = ScaledRecipeUiState.Error(e.message ?: "Error de conexión")
                Log.e("ScaledRecipesVM", "Excepción escalando receta", e)
            }
        }
    }

    /**
     * Guarda la receta escalada actual
     */
    fun saveScaledRecipe(recipeId: Long, desiredPortions: Int) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                val response = withContext(Dispatchers.IO) {
                    api.guardarRecetaEscalada(
                        id = recipeId,
                        porcionesDeseadas = desiredPortions
                    ).execute()
                }

                if (response.isSuccessful) {
                    _saveState.value = SaveState.Success("Receta guardada exitosamente")
                    loadSavedRecipes() // Recargar lista
                    Log.d("ScaledRecipesVM", "Receta guardada exitosamente")
                } else {
                    val error = response.errorBody()?.string() ?: "Error desconocido"
                    _saveState.value = SaveState.Error(error)
                    Log.e("ScaledRecipesVM", "Error guardando receta: ${response.code()} - $error")
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Error de conexión")
                Log.e("ScaledRecipesVM", "Excepción guardando receta", e)
            }
        }
    }

    /**
     * Carga todas las recetas guardadas del usuario
     */
    fun loadSavedRecipes() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.listarGuardadas().execute()
                }

                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()
                    _savedRecipes.value = recipes
                    _savedCount.value = recipes.size
                    Log.d("ScaledRecipesVM", "Recetas guardadas cargadas: ${recipes.size}/10")
                } else {
                    Log.e("ScaledRecipesVM", "Error cargando recetas guardadas: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ScaledRecipesVM", "Excepción cargando recetas guardadas", e)
            }
        }
    }

    /**
     * Elimina una receta guardada
     */
    fun deleteSavedRecipe(recipeId: Long) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.eliminarGuardada(recipeId).execute()
                }

                if (response.isSuccessful) {
                    loadSavedRecipes() // Recargar lista
                    Log.d("ScaledRecipesVM", "Receta eliminada exitosamente")
                } else {
                    Log.e("ScaledRecipesVM", "Error eliminando receta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ScaledRecipesVM", "Excepción eliminando receta", e)
            }
        }
    }

    /**
     * Resetea el estado de la receta escalada
     */
    fun resetScaledState() {
        _scaledRecipeState.value = ScaledRecipeUiState.Idle
    }

    /**
     * Resetea el estado de guardado
     */
    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    /**
     * Verifica si se puede guardar otra receta (límite de 10)
     */
    fun canSaveMore(): Boolean = _savedCount.value < 10
}

sealed class SaveState {
    object Idle : SaveState()
    object Loading : SaveState()
    data class Success(val message: String) : SaveState()
    data class Error(val message: String) : SaveState()
}