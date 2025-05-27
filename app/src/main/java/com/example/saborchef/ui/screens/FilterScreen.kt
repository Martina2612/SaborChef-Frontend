// File: app/src/main/java/com/example/saborchef/ui/screens/FilterScreen.kt
package com.example.saborchef.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.filled.Spa
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.ui.components.ChipItem
import com.example.saborchef.ui.theme.BlueDark
import androidx.compose.material3.FilterChip

import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.SearchViewModel

enum class Category(val label: String, val icon: ImageVector) {
    Snacks("Snacks", Icons.Default.Fastfood),
    Postres("Postres", Icons.Default.BakeryDining),
    Vegano("Vegano", Icons.Default.Spa),
    Carnes("Carnes", Icons.Default.Restaurant),
    Bebidas("Bebidas", Icons.Default.Coffee),
    Pastas("Pasta", Icons.Default.RamenDining),
    Veggie("Vegetariano", Icons.Default.EggAlt),
    Tartas("Tartas", Icons.Default.DinnerDining),
    Ensaladas("Ensalada", Icons.Default.Grass),
    Sopa("Sopa", Icons.Default.SoupKitchen)
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun FilterScreen(navController: NavController, viewModel: SearchViewModel) {
    var includeIngredients by remember { mutableStateOf("") }
    var excludeIngredients by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf("") }

    val allCategories = listOf(
        "Desayuno", "Almuerzo", "Pastas", "Cena",
        "Postres", "Snacks", "Vegetariano", "Vegano"
    )
    val selectedCategories = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filtros de Búsqueda", fontFamily = Poppins, color = BlueDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = BlueDark)
                    }
                },
                backgroundColor = Color.White
            )
        },
        content = { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Categorías", fontWeight = FontWeight.Bold)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allCategories.forEach { category ->
                        val isSelected = selectedCategories.contains(category)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedCategories.remove(category)
                                else selectedCategories.add(category)
                            },
                            label = { Text(category) }
                        )
                    }
                }

                OutlinedTextField(
                    value = includeIngredients,
                    onValueChange = { includeIngredients = it },
                    label = { Text("Ingredientes a incluir (coma separada)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = excludeIngredients,
                    onValueChange = { excludeIngredients = it },
                    label = { Text("Ingredientes a excluir (coma separada)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedUser,
                    onValueChange = { selectedUser = it },
                    label = { Text("Usuario") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val incluir = includeIngredients.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val excluir = excludeIngredients.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val tipos = if (selectedCategories.isEmpty()) null else selectedCategories.toList()

                        viewModel.applyFilters(
                            tipos = tipos,
                            incluir = incluir.ifEmpty { null },
                            excluir = excluir.ifEmpty { null },
                            usuario = selectedUser.takeIf { it.isNotBlank() }
                        )
                        navController.navigate("search") {
                            popUpTo("search") { inclusive = true }
                        } // vuelve a pantalla de resultados
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Aplicar filtros")
                }
            }
        }
    )
}


