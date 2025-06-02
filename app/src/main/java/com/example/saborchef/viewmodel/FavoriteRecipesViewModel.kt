package com.example.saborchef.ui.screens
/*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.R
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.UserRole
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.apis.FavoritaControllerApi
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.ui.components.RecipeCard
import com.example.saborchef.ui.components.SearchBar
import com.example.saborchef.ui.screens.FavoriteRecipesViewModel.FavoritesUiState
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar las recetas favoritas.
 */
class FavoriteRecipesViewModel(
    private val dataStore: DataStoreManager
) : ViewModel() {
    // API client para favoritos
    private val favoritaService: FavoritaControllerApi by lazy {
        ApiClient(baseUrl = "http://192.168.1.37:8080/")
            .createService(FavoritaControllerApi::class.java)
    }

    private val _uiState = mutableStateOf<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: State<FavoritesUiState> = _uiState

    init {
        fetchFavorites()
    }

    fun fetchFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading
            val userId = dataStore.readUserId() // Debes exponer método suspend
            favoritaService.listar(userId).enqueue(object : Callback<List<RecetaDetalleResponse>> {
                override fun onResponse(
                    call: Call<List<RecetaDetalleResponse>>,
                    response: Response<List<RecetaDetalleResponse>>
                ) {
                    if (response.isSuccessful) {
                        _uiState.value = FavoritesUiState.Success(response.body()!!)
                    } else {
                        _uiState.value = FavoritesUiState.Error("Error al cargar favoritas")
                    }
                }
                override fun onFailure(call: Call<List<RecetaDetalleResponse>>, t: Throwable) {
                    _uiState.value = FavoritesUiState.Error(t.localizedMessage ?: "Error de red")
                }
            })
        }
    }

    fun removeFavorite(recipeId: Long) {
        viewModelScope.launch {
            val userId = dataStore.readUserId()
            favoritaService.eliminar1(userId, recipeId).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    if (response.isSuccessful) fetchFavorites()
                }
                override fun onFailure(call: Call<Any>, t: Throwable) {}
            })
        }
    }

    sealed class FavoritesUiState {
        object Loading : FavoritesUiState()
        data class Success(val recetas: List<RecetaDetalleResponse>) : FavoritesUiState()
        data class Error(val message: String) : FavoritesUiState()
    }
}*/