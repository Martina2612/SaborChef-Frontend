// File: app/src/main/java/com/example/saborchef/ui/screens/SearchScreen.kt
package com.example.saborchef.ui.screens

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.ui.components.CategoryCard
import com.example.saborchef.ui.components.NoResultsView
import com.example.saborchef.ui.components.RecipeCard
import com.example.saborchef.ui.components.SearchBar
import com.example.saborchef.ui.components.SortDropdown
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.SearchUiState
import com.example.saborchef.viewmodel.SearchViewModel
import com.example.saborchef.ui.components.SuggestList

data class Recipe(
    val id: String,
    val title: String,
    val imageUrl: Uri,
    val duration: String,
    val portions: Int,
    val rating: Int,
    val user: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel) {
    // Cada vez que esta pantalla aparece, reseteamos la búsqueda
    LaunchedEffect(Unit) {
        viewModel.resetSearch()
    }

    // Estado local de la opción seleccionada en SortDropdown
    var sortOption by remember { mutableStateOf("") }
    val sortOptions = listOf("Más nueva a más antigua", "Nombre de usuario")

    // Leer uiState y query desde el ViewModel
    val uiState by remember { derivedStateOf { viewModel.uiState } }
    val query = viewModel.query

    // Mapeo a UI Recipe
    val recipesUi: List<Recipe> = when (uiState) {
        is SearchUiState.Results -> (uiState as SearchUiState.Results).recipes.map {
            Recipe(
                id = it.idReceta?.toString() ?: "0",
                title = it.nombre.orEmpty(),
                imageUrl = Uri.parse(it.fotoPrincipal.orEmpty()),
                duration = it.duracion?.toString().orEmpty(),
                portions = it.porciones ?: 0,
                rating = (it.promedioCalificacion ?: 0.0).toInt(),
                user = it.nombreUsuario.orEmpty()
            )
        }
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 4.dp,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Buscar",
                            fontFamily = Poppins,
                            color = BlueDark,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        // Aquí insertamos el SortDropdown a la derecha del título
                        SortDropdown(
                            options = sortOptions,
                            selected = sortOption,
                            onSelected = { newOption ->
                                sortOption = newOption
                                viewModel.onSortSelected(newOption)
                            }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = BlueDark
                        )
                    }
                },

            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ----------------------------------------------------------------
            // 1) En lugar del RecipeFilterSortBarView, usamos tu nuevo SearchBar:
            // ----------------------------------------------------------------
            SearchBar(
                query = query,
                onQueryChange = { viewModel.onQueryChange(it) },
                onSearch = { viewModel.searchByName() },
                onFilterClick = { navController.navigate("filter") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ----------------------------------------------------------------
            // 2) Mostramos el contenido según el estado (Idle, Suggest, Results, etc.)
            // ----------------------------------------------------------------
            when (uiState) {
                SearchUiState.Idle -> {
                    // Mostrar categorías
                    val categories = listOf(
                        "Desayuno" to com.example.saborchef.R.drawable.img_desayuno,
                        "Almuerzo" to com.example.saborchef.R.drawable.img_almuerzo,
                        "Pastas" to com.example.saborchef.R.drawable.img_pastas,
                        "Cena" to com.example.saborchef.R.drawable.img_cena,
                        "Postres" to com.example.saborchef.R.drawable.img_cheesecake,
                        "Snacks" to com.example.saborchef.R.drawable.img_snacks,
                        "Vegetariano" to com.example.saborchef.R.drawable.img_vegetariano,
                        "Vegano" to com.example.saborchef.R.drawable.img_vegano
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        gridItems(categories) { (label, drawableRes) ->
                            CategoryCard(
                                label = label,
                                imagePainter = painterResource(drawableRes),
                                modifier = Modifier
                                    .width(80.dp)   // Ajusta el ancho que prefieras
                                    .height(100.dp), // Ajusta el alto que prefieras
                                onClick = { viewModel.searchByCategory(label) }
                            )
                        }
                    }
                }
                is SearchUiState.Suggest -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listItems((uiState as SearchUiState.Suggest).suggestions) { suggestion ->
                            SuggestList(listOf(suggestion)) {
                                viewModel.onQueryChange(it)
                                viewModel.searchByName()
                            }
                        }
                    }
                }
                SearchUiState.NoResults -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        NoResultsView()
                    }
                }
                is SearchUiState.Results -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listItems(recipesUi) { r ->
                            RecipeCard(
                                id = r.id,
                                title = r.title,
                                imageUrl = r.imageUrl,
                                duration = r.duration,
                                portions = r.portions,
                                rating = r.rating,
                                user = r.user
                            ) {
                                navController.navigate("recipe/${r.id}")
                            }
                        }
                    }
                }
                is SearchUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ocurrió un error al cargar los datos",
                            color = Color.Red,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}
