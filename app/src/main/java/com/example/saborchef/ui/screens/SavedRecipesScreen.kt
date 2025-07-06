package com.example.saborchef.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.models.RecetaEscaladaResponse
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.ScaledRecipesViewModel
import kotlin.math.roundToInt
//creada recientemente, para probar ya estaba la de FavoriteRecipesScreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRecipesScreen(
    navController: NavController,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val scaledRecipesViewModel: ScaledRecipesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ScaledRecipesViewModel(context.applicationContext as Application) as T
            }
        }
    )

    val savedRecipes by scaledRecipesViewModel.savedRecipes.collectAsState()
    val savedCount by scaledRecipesViewModel.savedCount.collectAsState()

    LaunchedEffect(Unit) {
        scaledRecipesViewModel.loadSavedRecipes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mis Recetas",
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "$savedCount/10 guardadas",
                            fontFamily = Poppins,
                            fontSize = 12.sp,
                            color = BlueDark.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = BlueDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = BlueDark
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (savedRecipes.isEmpty()) {
                // Estado vacío
                EmptyRecipesState()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(savedRecipes) { recipe ->
                        SavedRecipeCard(
                            recipe = recipe,
                            onDelete = { recipeId ->
                                scaledRecipesViewModel.deleteSavedRecipe(recipeId)
                            },
                            onClick = { recipeId ->
                                navController.navigate("recipe/$recipeId")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRecipesState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = BlueDark.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sin recetas guardadas",
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = BlueDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Cuando ajustes las porciones de una receta\ny la guardes, aparecerá aquí",
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = BlueDark.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedRecipeCard(
    recipe: RecetaEscaladaResponse,
    onDelete: (Long) -> Unit,
    onClick: (Long) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        onClick = { recipe.idRecetaOriginal?.let { onClick(it) } },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con título y botón eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.nombreReceta ?: "Sin nombre",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BlueDark
                    )

                    Text(
                        text = "por ${recipe.nombreUsuario}",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = BlueDark.copy(alpha = 0.6f)
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info de escalado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Porciones
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = BlueDark.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.porcionesEscaladas} porciones",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                }

                // Factor de escalado
                Card(
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = OrangeDark.copy(alpha = 0.1f)
                    )
                ) {
                    Text(
                        text = "×${String.format("%.2f", recipe.factorEscalado)}",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Descripción (si existe)
            recipe.descripcionReceta?.let { descripcion ->
                if (descripcion.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = descripcion.take(100) + if (descripcion.length > 100) "..." else "",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = BlueDark.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }

    // Dialog de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Eliminar receta",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro que quieres eliminar \"${recipe.nombreReceta}\"?",
                    fontFamily = Poppins
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        recipe.idRecetaOriginal?.let { onDelete(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}