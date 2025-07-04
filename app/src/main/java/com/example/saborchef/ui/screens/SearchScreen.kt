package com.example.saborchef.ui.screens

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import com.example.saborchef.R
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.ui.components.CategoryCard
import com.example.saborchef.ui.components.NoResultsView
import com.example.saborchef.ui.components.RecipeCard
import com.example.saborchef.ui.components.SearchBar
import com.example.saborchef.ui.components.SortDropdown
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.SearchUiState
import com.example.saborchef.viewmodel.SearchViewModel

// Modelo local para UI
private data class RecipeItem(
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
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = viewModel(),
    role: Rol
) {
    LaunchedEffect(Unit) { viewModel.initIfNeeded() }

    var sortOption by remember { mutableStateOf("Más nueva a más antigua") }
    val sortOptions = listOf("Más nueva a más antigua", "Nombre de usuario")
    val uiState by remember { derivedStateOf { viewModel.uiState } }
    val query = viewModel.query

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Buscar",
                            fontFamily = Poppins,
                            color = BlueDark,
                            fontSize = 20.sp
                        )
                        if (uiState is SearchUiState.Results) {
                            SortDropdown(
                                options = sortOptions,
                                selected = sortOption,
                                onSelected = { sortOption = it }
                            )
                        }
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
                backgroundColor = Color.White,
                elevation = 4.dp
            )

        },
        bottomBar = { BottomBar(navController, role) }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Spacer(Modifier.height(12.dp))
            SearchBar(
                query = query,
                onQueryChange = { viewModel.onQueryChange(it) },
                onSearch = { viewModel.searchByName() },
                onFilterClick = { navController.navigate("filter") },
                placeholderText = "Busca tus recetas favoritas aqui"
            )
            Spacer(Modifier.height(8.dp))

            when (uiState) {
                SearchUiState.Idle -> {
                    val categories = listOf(
                        "Desayuno" to R.drawable.img_desayuno,
                        "Almuerzo" to R.drawable.img_almuerzo,
                        "Pastas" to R.drawable.img_pastas,
                        "Cena" to R.drawable.img_cena,
                        "Postres" to R.drawable.img_cheesecake,
                        "Snacks" to R.drawable.img_snacks,
                        "Vegetariano" to R.drawable.img_vegetariano,
                        "Vegano" to R.drawable.img_vegano
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
                                modifier = Modifier.size(80.dp, 100.dp),
                                onClick = { viewModel.searchByCategory(label) }
                            )
                        }
                    }
                }

                is SearchUiState.Suggest -> {
                    val suggestions = (uiState as SearchUiState.Suggest).suggestions
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listItems(suggestions) { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        viewModel.onQueryChange(suggestion)
                                        viewModel.searchByName()
                                    },
                                fontFamily = Poppins,
                                fontSize = 16.sp,
                                color = BlueDark
                            )
                            Divider()
                        }

                    }
                }

                SearchUiState.NoResults -> {
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        NoResultsView()
                    }
                }

                is SearchUiState.Results -> {
                    val original = (uiState as SearchUiState.Results).recipes.map {
                        RecipeItem(
                            id = it.idReceta.toString(),
                            title = it.nombre.orEmpty(),
                            imageUrl = Uri.parse(it.fotoPrincipal.orEmpty()),
                            duration = it.duracion.toString() +"min",
                            portions = it.porciones ?: 0,
                            rating = (it.promedioCalificacion ?: 0.0).toInt(),
                            user = it.nombreUsuario.orEmpty()
                        )
                    }
                    val sorted = remember(original, sortOption) {
                        when (sortOption) {
                            "Nombre de usuario" -> original.sortedBy { it.user }
                            else -> original.sortedByDescending { it.id.toLongOrNull() ?: 0L }
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listItems(sorted) { r ->
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
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Ocurrió un error al cargar los datos",
                            color = Color.Red,
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}
