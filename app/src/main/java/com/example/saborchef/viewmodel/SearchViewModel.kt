package com.example.saborchef.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.data.url
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.models.RecetaFiltroRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

sealed class SearchUiState {
    object Idle : SearchUiState()
    data class Suggest(val suggestions: List<String>) : SearchUiState()
    object NoResults : SearchUiState()
    data class Results(val recipes: List<RecetaDetalleResponse>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel : ViewModel() {

    var query by mutableStateOf("")
        private set

    var uiState by mutableStateOf<SearchUiState>(SearchUiState.Idle)
        private set

    var sortOption by mutableStateOf("Más nueva a más antigua")
        private set

    var suggestions by mutableStateOf<List<String>>(emptyList())
        private set

    private val api: RecetaControllerApi by lazy {
        ApiClient(baseUrl = url)
            .createService(RecetaControllerApi::class.java)
    }

    private var initialized = false

    fun initIfNeeded() {
        if (!initialized) {
            resetSearch()
            initialized = true
        }
    }

    fun onQueryChange(new: String) {
        query = new
        if (new.isBlank()) {
            suggestions = emptyList()
            uiState = SearchUiState.Idle
            return
        }

        viewModelScope.launch {
            delay(300)
            if (query != new) return@launch

            try {
                val resp = withContext(Dispatchers.IO) {
                    api.sugerirNombres(new).execute()
                }
                if (resp.isSuccessful) {
                    val list = resp.body().orEmpty()
                    suggestions = list.distinct()
                    uiState = if (list.isEmpty()) SearchUiState.NoResults
                    else SearchUiState.Suggest(list)
                } else {
                    uiState = SearchUiState.Error("Error servidor: ${resp.code()}")
                }
            } catch (e: Exception) {
                uiState = SearchUiState.Error("Error conexión: ${e.message}")
            }
        }
    }

    fun searchByName() {
        if (query.isBlank()) return

        viewModelScope.launch {
            try {
                val resp = withContext(Dispatchers.IO) {
                    api.buscarPorNombre(query, sortOption).execute()
                }
                handleRecipeResponse(resp)
            } catch (e: Exception) {
                uiState = SearchUiState.Error("Error conexión: ${e.message}")
            }
        }
    }

    fun searchByCategory(tipo: String) {
        query = ""
        viewModelScope.launch {
            try {
                val tipoUpper = tipo.uppercase(Locale.getDefault())
                val resp = withContext(Dispatchers.IO) {
                    api.buscarPorTipo(tipoUpper, sortOption).execute()
                }
                handleRecipeResponse(resp)
            } catch (e: Exception) {
                uiState = SearchUiState.Error("Error conexión: ${e.message}")
            }
        }
    }

    fun applyFilters(
        tipos: List<String>? = null,
        incluir: List<String>? = null,
        excluir: List<String>? = null,
        usuarios: List<String>? = null
    ) {
        val tiposUpper = tipos?.map { it.uppercase(Locale.getDefault()) }?.takeIf { it.isNotEmpty() }
        val incluirList = incluir?.map { it.lowercase(Locale.getDefault()) }?.takeIf { it.isNotEmpty() }
        val excluirList = excluir?.map { it.lowercase(Locale.getDefault()) }?.takeIf { it.isNotEmpty() }
        val usuariosList = usuarios?.takeIf { it.isNotEmpty() }

        val req = RecetaFiltroRequest(
            nombre = null,
            tipo = tiposUpper,
            ingredientesIncluidos = incluirList,
            ingredientesExcluidos = excluirList,
            usuario = usuariosList,
            orden = sortOption
        )

        viewModelScope.launch {
            try {
                val resp = withContext(Dispatchers.IO) {
                    api.buscarPorFiltros(req).execute()
                }
                handleRecipeResponse(resp)
            } catch (e: Exception) {
                uiState = SearchUiState.Error("Error conexión: ${e.message}")
            }
        }
    }

    fun onSortSelected(option: String) {
        sortOption = option
        when (val st = uiState) {
            is SearchUiState.Results -> {
                if (query.isNotBlank()) searchByName()
                else searchByCategory(st.recipes.firstOrNull()?.tipo ?: "")
            }
            is SearchUiState.Suggest -> onQueryChange(query)
            else -> {}
        }
    }

    private fun handleRecipeResponse(response: retrofit2.Response<List<RecetaDetalleResponse>>) {
        if (response.isSuccessful) {
            val list = response.body().orEmpty()
            uiState = when {
                list.isEmpty() -> SearchUiState.NoResults
                else           -> SearchUiState.Results(list)
            }
        } else {
            uiState = SearchUiState.Error("Error servidor: ${response.code()}")
        }
    }

    fun resetSearch() {
        query = ""
        suggestions = emptyList()
        uiState = SearchUiState.Idle
    }
}
