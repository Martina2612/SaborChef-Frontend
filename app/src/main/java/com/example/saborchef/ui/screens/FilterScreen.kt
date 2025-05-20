package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.OrangeLight
import com.example.saborchef.ui.theme.OrangeSoft
import com.example.saborchef.ui.theme.Poppins
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.saborchef.ui.components.ChipItem


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


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    navController: NavController,
    selectedInclude: List<String>,
    selectedExclude: List<String>,
    selectedUsers: String?,
    onIncludeChange: (String) -> Unit,
    onExcludeChange: (String) -> Unit,
    onUserSelect: () -> Unit,
    onApply: () -> Unit
) {
    var selectedCategories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var includeInput by remember { mutableStateOf("") }
    var excludeInput by remember { mutableStateOf("") }
    var chefInput by remember {mutableStateOf("")}
    var selectedInclude by remember { mutableStateOf(selectedInclude.toMutableList()) }
    var selectedExclude by remember { mutableStateOf(selectedExclude.toMutableList()) }
    var selectedChef by remember { mutableStateOf(selectedUsers ?: "") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = OrangeDark,
                title = {
                    Text("Filtros", color = Color.White, fontFamily = Poppins, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                elevation = 4.dp
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
                Spacer(modifier = Modifier.height(20.dp))
                Text("Categorías", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize =16.sp, color = BlueDark)
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp), // ajustalo según lo que necesites
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false // evita que haya scroll dentro del grid
                ) {
                    items(Category.values()) { category ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        if (selectedCategories.contains(category)) OrangeDark else OrangeLight,
                                        RoundedCornerShape(52)
                                    )
                                    .clickable {
                                        selectedCategories = if (selectedCategories.contains(category))
                                            selectedCategories - category
                                        else
                                            selectedCategories + category
                                    }
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.label,
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = category.label,
                                fontFamily = Poppins,
                                fontSize = 11.sp,
                                color = BlueDark,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = OrangeDark, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Mostrar recetas con:", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize =16.sp, color = BlueDark)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = includeInput,
                    onValueChange = { includeInput = it },
                    placeholder = { Text("Ingrediente...",fontFamily = Poppins, fontSize =14.sp)  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = OrangeDark,
                        cursorColor = OrangeDark
                    ),
                    shape = RoundedCornerShape(30.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (includeInput.isNotBlank()) {
                            selectedInclude = (selectedInclude + includeInput.trim()) as MutableList<String>
                            includeInput = ""
                            keyboardController?.hide()
                        }
                    })
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedInclude.forEach { item ->
                        ChipItem(text = item) {
                            selectedInclude = (selectedInclude - item) as MutableList<String>
                        }
                    }
                }

                Divider(color = OrangeDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                // Ingredientes a excluir
                Text("Mostrar recetas sin:", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize =16.sp, color = BlueDark)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = excludeInput,
                    onValueChange = { excludeInput = it },
                    placeholder = { Text("Ingrediente...",fontFamily = Poppins, fontSize =14.sp)  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = OrangeDark,
                        cursorColor = OrangeDark
                    ),
                    shape = RoundedCornerShape(30.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (excludeInput.isNotBlank()) {
                            selectedExclude = (selectedExclude + excludeInput.trim()) as MutableList<String>
                            excludeInput = ""
                            keyboardController?.hide()
                        }
                    })
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedExclude.forEach { item ->
                        ChipItem(text = item) {
                            selectedExclude = (selectedExclude - item) as MutableList<String>
                        }
                    }
                }

                Divider(color = OrangeDark, thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

                Text("Nuestros Chef's", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize =16.sp, color = BlueDark)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = chefInput,
                    onValueChange = { chefInput = it },
                    placeholder = { Text("Elige uno de nuestros usuarios...",fontFamily = Poppins, fontSize =14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = OrangeDark,
                        cursorColor = OrangeDark
                    ),
                    shape = RoundedCornerShape(30.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (selectedChef.isNotBlank()) {
                            onUserSelect()
                        }
                    })
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (selectedChef.isNotBlank()) {
                    ChipItem(text = selectedChef) {
                        selectedChef = ""
                    }
                }

                Spacer(Modifier.height(24.dp))
                AppButton(
                    text = "Aplicar",
                    onClick = onApply,
                    primary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterScreenPreview() {
    val navController = rememberNavController()
    FilterScreen(
        navController = navController,
        selectedInclude = listOf("Huevo", "Tomate"),
        selectedExclude = listOf("Maní"),
        selectedUsers = "usuario42",
        onIncludeChange = {},
        onExcludeChange = {},
        onUserSelect = {},
        onApply = {}
    )
}
