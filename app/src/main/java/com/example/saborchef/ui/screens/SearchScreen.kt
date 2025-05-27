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
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.R
import com.example.saborchef.ui.components.*
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.SearchUiState
import com.example.saborchef.viewmodel.SearchViewModel

data class Recipe(
    val id: String,
    val title: String,
    val imageUrl: Uri,
    val duration: String,
    val portions: Int,
    val rating: Int,
    val user: String
)

@Composable
fun SearchScreen(navController: NavController,viewModel: SearchViewModel) {
    val viewModel: SearchViewModel = viewModel()

    var sortOption by remember { mutableStateOf(viewModel.sortOption) }
    val sortOptions = listOf("Más nueva a más antigua", "Nombre de usuario")

    val uiState by remember { derivedStateOf { viewModel.uiState } }
    val query = viewModel.query

    // Mapeo a UI Recipe
    val recipesUi = when (uiState) {
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
                title = {
                    Text("Buscar", fontFamily = Poppins, color = BlueDark, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = BlueDark)
                    }
                }
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            RecipeFilterSortBarView(
                query = query,
                sortOption = sortOption,
                sortOptions = sortOptions,
                onQueryChange = { viewModel.onQueryChange(it) },
                onSortSelected = {
                    sortOption = it
                    viewModel.onSortSelected(it)
                },
                onFilterClick = {
                    navController.navigate("filter")
                },
                onSearch = {
                    viewModel.searchByName()
                }
            )

            Spacer(Modifier.height(8.dp))

            when (uiState) {
                SearchUiState.Idle -> {
                    val categories = listOf(
                        "Desayuno" to R.drawable.img_cheesecake,
                        "Almuerzo" to R.drawable.img_cheesecake,
                        "Pastas" to R.drawable.img_cheesecake,
                        "Cena" to R.drawable.img_cheesecake,
                        "Postres" to R.drawable.img_cheesecake,
                        "Snacks" to R.drawable.img_cheesecake,
                        "Vegetariano" to R.drawable.img_cheesecake,
                        "Vegano" to R.drawable.img_cheesecake
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        gridItems(categories) { (label, drawable) ->
                            CategoryCard(
                                label = label,
                                imagePainter = painterResource(drawable),
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(200.dp),
                                onClick = { viewModel.searchByCategory(label) }
                            )
                        }
                    }
                }
                is SearchUiState.Suggest -> {
                    LazyColumn(
                        Modifier.weight(1f),
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
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        NoResultsView()
                    }
                }
                is SearchUiState.Results -> {
                    LazyColumn(
                        Modifier
                            .weight(1f)
                            .padding(16.dp),
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
                            ) { navController.navigate("recipe/${r.id}") }
                        }
                    }
                }
                is SearchUiState.Error -> {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("Ocurrió un error al cargar los datos", color = Color.Red)
                        // O podés usar una vista custom, si tenés algo como:
                        // ErrorView(message = uiState.message)
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeFilterSortBarView(
    query: String,
    sortOption: String,
    sortOptions: List<String>,
    onQueryChange: (String) -> Unit,
    onSortSelected: (String) -> Unit,
    onFilterClick: () -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            placeholder = { Text("Buscar receta...") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onSearch) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                }
            }
        )

        IconButton(onClick = onFilterClick) {
            Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
        }

        DropdownMenuWrapper(
            options = sortOptions,
            selectedOption = sortOption,
            onOptionSelected = onSortSelected
        )
    }
}

@Composable
fun DropdownMenuWrapper(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Sort, contentDescription = "Ordenar")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onOptionSelected(option)
                }) {
                    Text(option)
                }
            }
        }
    }
}

