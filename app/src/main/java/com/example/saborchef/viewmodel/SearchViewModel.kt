package com.example.saborchef.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.RecetaFiltroRequest
import com.example.saborchef.models.RecetaResumenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SearchUiState {
    object Idle : SearchUiState()
    data class Suggest(val suggestions: List<String>) : SearchUiState()
    object NoResults : SearchUiState()
    data class Results(val recipes: List<RecetaResumenResponse>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel : ViewModel() {

    var query by mutableStateOf("")
        private set

    var uiState by mutableStateOf<SearchUiState>(SearchUiState.Idle)
        private set

    var sortOption by mutableStateOf("Más nueva a más antigua")
        private set

    private val api: RecetaControllerApi by lazy {
        ApiClient(baseUrl = "http://192.168.1.37:8080/")
            .createService(RecetaControllerApi::class.java)
    }

    fun onQueryChange(new: String) {
        Log.d("SearchViewModel", "onQueryChange() called with: $new")
        query = new
        if (new.isBlank()) {
            Log.d("SearchViewModel", "Query está en blanco, cambiando estado a Idle")
            uiState = SearchUiState.Idle
            return
        }

        viewModelScope.launch {
            try {
                val request = RecetaFiltroRequest(
                    nombre = null,
                    tipo = null,
                    ingredientesIncluidos = listOf(new),
                    ingredientesExcluidos = null,
                    usuario = null,
                    orden = sortOption
                )

                Log.d("SearchViewModel", "Realizando búsqueda de sugerencias con filtro: $request")
                val response = withContext(Dispatchers.IO) {
                    api.buscarPorFiltros(request).execute()
                }

                if (response.isSuccessful) {
                    val suggestions = response.body().orEmpty().mapNotNull { it.nombre }.distinct()
                    Log.d("SearchViewModel", "Sugerencias encontradas: $suggestions")
                    uiState = if (suggestions.isEmpty())
                        SearchUiState.NoResults
                    else
                        SearchUiState.Suggest(suggestions)
                } else {
                    Log.d("SearchViewModel", "Respuesta no exitosa: ${response.code()}")
                    uiState = SearchUiState.Error("Error del servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error en onQueryChange: ${e.message}", e)
                uiState = SearchUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun searchByName() {
        Log.d("SearchViewModel", "searchByName() called con query: '$query'")
        if (query.isBlank()) {
            Log.d("SearchViewModel", "Query está en blanco, no se busca")
            return
        }

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.buscarPorNombre(query, sortOption).execute()
                }
                Log.d("SearchViewModel", "Response buscarPorNombre: ${response.code()}, body: ${response.body()}")

                handleRecipeResponse(response)
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error en searchByName: ${e.message}", e)
                uiState = SearchUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun searchByCategory(tipo: String) {
        Log.d("SearchViewModel", "searchByCategory() llamada con tipo: $tipo")
        query = ""

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.buscarPorTipo(tipo, sortOption).execute()
                }
                Log.d("SearchViewModel", "Response buscarPorTipo: ${response.code()}, body: ${response.body()}")

                handleRecipeResponse(response)
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error en searchByCategory: ${e.message}", e)
                uiState = SearchUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun applyFilters(
        tipos: List<String>? = null,
        incluir: List<String>? = null,
        excluir: List<String>? = null,
        usuario: String? = null
    ) {
        Log.d("SearchViewModel", "applyFilters() llamada con: tipos=$tipos, incluir=$incluir, excluir=$excluir, usuario=$usuario")
        val req = RecetaFiltroRequest(
            nombre = null,
            tipo = tipos,
            ingredientesIncluidos = incluir,
            ingredientesExcluidos = excluir,
            usuario = usuario?.let { listOf(it) },
            orden = sortOption
        )

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.buscarPorFiltros(req).execute()
                }
                Log.d("SearchViewModel", "Response buscarPorFiltros con filtros aplicados: ${response.code()}, body: ${response.body()}")

                handleRecipeResponse(response)
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error en applyFilters: ${e.message}", e)
                uiState = SearchUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun onSortSelected(option: String) {
        Log.d("SearchViewModel", "onSortSelected() llamada con opción: $option")
        sortOption = option

        when (val st = uiState) {
            is SearchUiState.Results -> {
                if (query.isNotBlank()) {
                    Log.d("SearchViewModel", "onSortSelected: ejecutando searchByName()")
                    searchByName()
                } else {
                    Log.d("SearchViewModel", "onSortSelected: ejecutando searchByCategory() con tipo: ${st.recipes.firstOrNull()?.tipo}")
                    searchByCategory(st.recipes.firstOrNull()?.tipo ?: "")
                }
            }
            is SearchUiState.Suggest -> {
                Log.d("SearchViewModel", "onSortSelected: ejecutando onQueryChange()")
                onQueryChange(query)
            }
            else -> {
                Log.d("SearchViewModel", "onSortSelected: no se realiza ninguna acción")
            }
        }
    }

    private fun handleRecipeResponse(response: retrofit2.Response<List<RecetaResumenResponse>>) {
        Log.d("SearchViewModel", "handleRecipeResponse - code: ${response.code()}, body: ${response.body()}")
        val list = response.body().orEmpty()
        uiState = if (response.isSuccessful && list.isNotEmpty()) {
            Log.d("SearchViewModel", "Recetas encontradas: ${list.size}")
            SearchUiState.Results(list)
        } else if (response.isSuccessful && list.isEmpty()) {
            Log.d("SearchViewModel", "Respuesta vacía")
            SearchUiState.NoResults
        } else {
            Log.e("SearchViewModel", "Error del servidor con código: ${response.code()}")
            SearchUiState.Error("Error del servidor: ${response.code()}")
        }
    }
}
