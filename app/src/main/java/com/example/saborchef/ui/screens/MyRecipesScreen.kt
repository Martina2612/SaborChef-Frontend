package com.example.saborchef.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.R
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.url
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.viewmodel.MyRecipesUiState
import com.example.saborchef.viewmodel.MyRecipesViewModel
import com.example.saborchef.viewmodel.MyRecipesViewModelFactory
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.ui.components.CurvedHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.saborchef.ui.components.RecipeCard
import com.example.saborchef.ui.theme.Poppins

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyRecipesScreen(
    navController: NavController,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    val viewModel: MyRecipesViewModel = viewModel(
        factory = MyRecipesViewModelFactory(
            dataStore = DataStoreManager(context)
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    var showConfirm by remember { mutableStateOf(false) }
    var showDeleted by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<RecetaDetalleResponse?>(null) }
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()
    LaunchedEffect(showConfirm) {
        if (showConfirm) sheetState.show() else sheetState.hide()
    }

    Scaffold(
        topBar = {
            CurvedHeader(
                title = "Mis recetas",
                icon = Icons.Default.Book,
                headerColor = OrangeDark,
                circleColor = Color.White,
                onBack = onBack
            )
        }

    ) {
        when (uiState) {
            MyRecipesUiState.Loading -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color=OrangeDark)
                }
            }
            is MyRecipesUiState.Error -> {
                val msg = (uiState as MyRecipesUiState.Error).message
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Error: $msg", color = MaterialTheme.colors.error)
                }
            }
            is MyRecipesUiState.Success -> {
                val recipes = (uiState as MyRecipesUiState.Success).recipes
                val pageSize = 4
                var currentPage by remember { mutableStateOf(0) }
                val totalPages = (recipes.size + pageSize - 1) / pageSize
                val pagedRecipes = recipes.drop(currentPage * pageSize).take(pageSize)
                Log.d("MyRecipesScreen", "Recetas cargadas: ${'$'}{recipes.size}")
                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    sheetContent = {
                        Column(
                            Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "¿Está seguro que desea eliminar esta receta?",
                                style = MaterialTheme.typography.h6,
                                color = BlueDark,
                                fontFamily = Poppins
                            )
                            Spacer(Modifier.height(16.dp))
                            Row {
                                Button(
                                    onClick = {
                                        toDelete?.let { viewModel.deleteRecipe(it.idReceta ?: 0) }
                                        showConfirm = false
                                        showDeleted = true
                                        scope.launch {
                                            delay(1500)
                                            showDeleted = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(backgroundColor = OrangeDark),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("Sí, eliminar", color = MaterialTheme.colors.onPrimary)
                                }
                                Spacer(Modifier.width(8.dp))
                                OutlinedButton(onClick = { showConfirm = false }, shape = RoundedCornerShape(50)) {
                                    Text("No, cancelar")
                                }
                            }
                        }
                    }
                ) {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pagedRecipes, key = { it.idReceta.toString() }) { r ->
                            val base64 = r.fotoPrincipal
                            Log.d("MyRecipesScreen", "Receta ID=${r.idReceta} nombre=${r.nombre} base64-valido=${!base64.isNullOrEmpty()} length=${base64?.length ?: 0}")
                            Box(Modifier.fillMaxWidth()
                                .height(120.dp)) {
                                // Tu RecipeCard consume Base64
                                RecipeCard(
                                    id = r.idReceta.toString(),
                                    title = r.nombre.toString(),
                                    imageUrl = Uri.parse(r.fotoPrincipal ?: ""),
                                    duration = "${r.duracion} min",
                                    portions = r.porciones ?: 0,
                                    rating = r.promedioCalificacion?.toInt() ?: 0,
                                    user = r.nombreUsuario ?: "",
                                    onClick = { navController.navigate("recipe/${r.idReceta}")}
                                )
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            Log.d("MyRecipes", "Editando receta con ID: ${r.idReceta}")
                                            navController.currentBackStackEntry?.savedStateHandle?.set("recetaSeleccionada", r)
                                            navController.navigate("edit_recipe")
                                        }
                                    )
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = BlueDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { toDelete = r; showConfirm = true }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = OrangeDark)
                                    }
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { if (currentPage > 0) currentPage-- },
                                    enabled = currentPage > 0
                                ) {
                                    Text("Anterior")
                                }

                                Text("Página ${currentPage + 1} de $totalPages")

                                OutlinedButton(
                                    onClick = { if (currentPage < totalPages - 1) currentPage++ },
                                    enabled = currentPage < totalPages - 1
                                ) {
                                    Text("Siguiente")
                                }
                            }
                        }

                    }
                    AnimatedVisibility(visible = showDeleted) {
                        Box(
                            Modifier.fillMaxSize().background(Color(0x88000000)),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(shape = RoundedCornerShape(16.dp), elevation = 8.dp) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(24.dp)
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.chef_popup),
                                        contentDescription = null,
                                        modifier = Modifier.size(120.dp)
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text("Receta eliminada!", style = MaterialTheme.typography.h6, color = BlueDark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

