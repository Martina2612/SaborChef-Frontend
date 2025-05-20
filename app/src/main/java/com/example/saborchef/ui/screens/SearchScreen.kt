package com.example.saborchef.ui.screens

import BottomBar
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saborchef.R
import com.example.saborchef.model.UserRole
import com.example.saborchef.ui.components.*
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import androidx.compose.ui.tooling.preview.Preview

sealed class SearchUiState {
    object Idle : SearchUiState()
    data class Suggest(val suggestions: List<String>) : SearchUiState()
    object NoResults : SearchUiState()
    data class Results(val recipes: List<Recipe>) : SearchUiState()
}
data class Recipe(
    val id: String, val title: String, val imageUrl: Uri,
    val duration: String, val portions: Int, val rating: Int, val user:String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    navController: NavController,
    role: UserRole,
    uiState: SearchUiState,
    query: String,
    selectedTags: List<String>,
    onQueryChange: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onSortSelected: (String) -> Unit,
    onFilterClick: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    var sortOption by remember { mutableStateOf("Más nueva a más antigua") }
    val sortOptions = listOf("Más nueva a más antigua", "Nombre de usuario")

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                title = {
                    Text(
                        text = "Buscar",
                        fontFamily = Poppins,
                        color = BlueDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = BlueDark)
                    }
                },
                actions = {
                    // sólo muestro el dropdown si ya estoy en estado Results
                    if (uiState is SearchUiState.Results) {
                        SortDropdown(
                            options = sortOptions,
                            selected = sortOption,
                            onSelected = {
                                sortOption = it
                                onSortSelected(it)
                            }
                        )
                    }
                }
            )

        },


        bottomBar = { BottomBar(navController, role) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            SearchBar(query, onQueryChange, onFilterClick)
            Spacer(Modifier.width(8.dp))


            // 2) Chips
            if (selectedTags.isNotEmpty()) {
                FlowRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedTags.forEach { tag ->
                        ChipItem(text = tag, onRemove = { onRemoveTag(tag) })
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // 3) Contenido principal usa weight(1f)
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
                        items(categories) { (label, drawable) ->
                            CategoryCard(
                                label = label,
                                imagePainter = painterResource(id = drawable),
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(200.dp),
                                onClick = onCategorySelect
                            )
                        }
                    }
                }
                is SearchUiState.Suggest -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(uiState.suggestions) { suggestion ->
                            SuggestList(listOf(suggestion)) { onSuggestionClick(suggestion) }
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
                        items(uiState.recipes) { r ->
                            RecipeCard(
                                id = r.id,
                                title = r.title,
                                imageUrl = r.imageUrl,
                                duration = r.duration,
                                portions = r.portions,
                                rating =r.rating,
                                user =r.user,
                                onClick = onRecipeClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewIdle() {
    SearchScreen(
        navController = rememberNavController(),
        role = UserRole.USUARIO,
        uiState = SearchUiState.Idle,
        query = "",
        selectedTags = emptyList(),
        onQueryChange = {},
        onRemoveTag = {},
        onCategorySelect = {},
        onSuggestionClick = {},
        onSortSelected = {},
        onFilterClick = {},
        onRecipeClick = {}
    )
}
