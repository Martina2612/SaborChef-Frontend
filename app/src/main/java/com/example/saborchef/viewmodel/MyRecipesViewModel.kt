// File: MyRecipesViewModel.kt
package com.example.saborchef.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.SessionManager
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.data.url
import com.example.saborchef.models.RecetaDetalleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class MyRecipesUiState {
    object Loading : MyRecipesUiState()
    data class Success(val recipes: List<RecetaDetalleResponse>) : MyRecipesUiState()
    data class Error(val message: String) : MyRecipesUiState()
}

class MyRecipesViewModel(
    private val dataStore: DataStoreManager,
    private val api: RecetaControllerApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyRecipesUiState>(MyRecipesUiState.Loading)
    val uiState: StateFlow<MyRecipesUiState> = _uiState.asStateFlow()

    init {
        fetchMyRecipes()
    }

    private fun fetchMyRecipes() {
        viewModelScope.launch {
            _uiState.value = MyRecipesUiState.Loading
            try {
                val userId = dataStore.userId.firstOrNull() ?: 0L
                val response = withContext(Dispatchers.IO) {
                    api.buscarPorUsuarioId(userId, "fechaDesc").execute()
                }

                if (response.isSuccessful) {
                    val recetas = response.body().orEmpty()
                    _uiState.value = MyRecipesUiState.Success(recetas)
                } else {
                    _uiState.value = MyRecipesUiState.Error("Error ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MyRecipesVM", "Exception fetching recipes", e)
                _uiState.value = MyRecipesUiState.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun deleteRecipe(idReceta: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.eliminar(idReceta).execute()
                Log.d("MyRecipesVM", "Código respuesta DELETE: ${response.code()}")
                Log.d("MyRecipesVM", "Body: ${response.body()}, ErrorBody: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    fetchMyRecipes()
                } else {
                    Log.e("MyRecipesVM", "Error al eliminar: código ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MyRecipesVM", "Excepción al eliminar", e)
            }
        }
    }

}

class MyRecipesViewModelFactory(
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Usamos runBlocking para obtener el token sin suspender
        val token = kotlinx.coroutines.runBlocking {
            dataStore.token.firstOrNull()
        }

        // Guardamos el token en SessionManager
        SessionManager.token = token

        // Creamos el cliente con autenticación
        val api = ApiClient.createAuthenticatedClient(url)
            .createService(RecetaControllerApi::class.java)

        @Suppress("UNCHECKED_CAST")
        return MyRecipesViewModel(dataStore, api) as T
    }
}

