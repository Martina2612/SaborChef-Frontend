package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.ChipItem
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.OrangeLight
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.SearchViewModel

// Definimos nuestro propio enum Category con label e icon
enum class Category(val label: String, val icon: ImageVector) {
    Snacks("Snacks", Icons.Filled.Fastfood),
    Postres("Postres", Icons.Filled.BakeryDining),
    Vegano("Vegano", Icons.Filled.Spa),
    Carnes("Carne", Icons.Filled.Restaurant),
    Bebidas("Bebidas", Icons.Filled.Coffee),
    Pastas("Pastas", Icons.Filled.RamenDining),
    Vegetariano("Vegetariano", Icons.Filled.Grass),
    Tartas("Tartas", Icons.Filled.DinnerDining),
    Ensaladas("Ensalada", Icons.Filled.Grass),
    Sopa("Sopas", Icons.Filled.SoupKitchen)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    navController: NavController,
    viewModel: SearchViewModel
) {
    // Inputs
    var includeInput by remember { mutableStateOf("") }
    var excludeInput by remember { mutableStateOf("") }
    var chefInput by remember { mutableStateOf("") }

    // Selected lists
    var selectedCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var selectedInclude by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedExclude by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedChefs by remember { mutableStateOf<List<String>>(emptyList()) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = OrangeDark,
                title = {
                    Text(
                        "Filtros",
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            item {
                // Categories Section
                Spacer(Modifier.height(12.dp))
                Text(
                    "Categorías",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = BlueDark
                )
                Spacer(Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    userScrollEnabled = false
                ) {
                    items(Category.values()) { cat ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        if (selectedCategories.contains(cat)) OrangeDark else OrangeLight,
                                        RoundedCornerShape(52)
                                    )
                                    .clickable {
                                        selectedCategories = if (selectedCategories.contains(cat))
                                            selectedCategories - cat
                                        else
                                            selectedCategories + cat
                                    }
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(cat.icon, contentDescription = cat.label, tint = Color.White)
                            }
                            Text(
                                text = cat.label,
                                fontFamily = Poppins,
                                fontSize = 11.sp,
                                color = BlueDark,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Divider(color = OrangeDark, thickness = 1.dp)
                Spacer(Modifier.height(16.dp))

                // Prepare lists for apply
                val tiposList: List<String> = selectedCategories.map { it.label }

                // FilterChipsSection usages...
                FilterChipsSection(
                    label = "Mostrar recetas con:",
                    text = includeInput,
                    onTextChange = { includeInput = it },
                    placeholder = "Ingrediente...",
                    onAdd = {
                        if (includeInput.isNotBlank()) {
                            selectedInclude = selectedInclude + includeInput.trim()
                            includeInput = ""
                            keyboardController?.hide()
                        }
                    },
                    items = selectedInclude,
                    onRemove = { selectedInclude = selectedInclude - it }
                )

                Spacer(Modifier.height(16.dp))
                Divider(color = OrangeDark, thickness = 1.dp)
                Spacer(Modifier.height(16.dp))

                FilterChipsSection(
                    label = "Mostrar recetas sin:",
                    text = excludeInput,
                    onTextChange = { excludeInput = it },
                    placeholder = "Ingrediente...",
                    onAdd = {
                        if (excludeInput.isNotBlank()) {
                            selectedExclude = selectedExclude + excludeInput.trim()
                            excludeInput = ""
                            keyboardController?.hide()
                        }
                    },
                    items = selectedExclude,
                    onRemove = { selectedExclude = selectedExclude - it }
                )

                Spacer(Modifier.height(16.dp))
                Divider(color = OrangeDark, thickness = 1.dp)
                Spacer(Modifier.height(16.dp))

                FilterChipsSection(
                    label = "Nuestros Chefs",
                    text = chefInput,
                    onTextChange = { chefInput = it },
                    placeholder = "Nombre del chef...",
                    onAdd = {
                        if (chefInput.isNotBlank()) {
                            selectedChefs = selectedChefs + chefInput.trim()
                            chefInput = ""
                            keyboardController?.hide()
                        }
                    },
                    items = selectedChefs,
                    onRemove = { selectedChefs = selectedChefs - it }
                )

                Spacer(Modifier.height(24.dp))
                AppButton(
                    text = "Aplicar",
                    onClick = {
                        viewModel.applyFilters(
                            tipos = if (tiposList.isNotEmpty()) tiposList else null,
                            incluir = if (selectedInclude.isNotEmpty()) selectedInclude else null,
                            excluir = if (selectedExclude.isNotEmpty()) selectedExclude else null,
                            usuarios = if (selectedChefs.isNotEmpty()) selectedChefs else null
                        )
                        navController.popBackStack()
                    },
                    primary = true,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChipsSection(
    label: String,
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    onAdd: () -> Unit,
    items: List<String>,
    onRemove: (String) -> Unit
) {
    Text(label, fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = BlueDark)
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text(placeholder, fontFamily = Poppins, fontSize = 14.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onAdd() }),
        modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)),
        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = OrangeDark, cursorColor = OrangeDark),
        shape = RoundedCornerShape(30.dp)
    )
    Spacer(Modifier.height(8.dp))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            ChipItem(text = item) { onRemove(item) }
        }
    }
}


