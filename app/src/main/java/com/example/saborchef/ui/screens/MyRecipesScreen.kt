package com.example.saborchef.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saborchef.R
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyRecipesScreen(
    navController: NavController,
    recipesInitial: List<RecipeData> = sampleRecipes()
) {
    var recipes by remember { mutableStateOf(recipesInitial) }
    var showConfirm by remember { mutableStateOf(false) }
    var showDeleted by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<RecipeData?>(null) }
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
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
                onBack = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            AppButton(
                text = "Añadir receta",
                onClick = { /* Navegar a creación */ },
                primary = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "¿Está seguro que desea eliminar esta receta?",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        color = BlueDark
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            recipes = recipes.filterNot { it.id == toDelete?.id }
                            showConfirm = false
                            showDeleted = true
                            scope.launch {
                                delay(2000)
                                showDeleted = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = OrangeDark),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Sí, eliminar", color = Color.White, fontFamily = Poppins)
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showConfirm = false },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("No, cancelar", fontFamily = Poppins)
                    }
                }
            }
        ) {
            Box(Modifier.fillMaxSize()) {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCardRow(
                            recipe = recipe,
                            onDelete = {
                                toDelete = recipe
                                showConfirm = true
                            },
                            onEdit = {
                                /* navegar a edición */
                            }
                        )
                    }
                }
                AnimatedVisibility(
                    visible = showDeleted,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0x88000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = 8.dp
                        ) {
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
                                Text(
                                    "Receta eliminada!",
                                    fontFamily = Poppins,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = BlueDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeCardRow(
    recipe: RecipeData,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
    ) {
        Row(Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(recipe.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(
                Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = recipe.title,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BlueDark
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { idx ->
                        if (idx < recipe.rating)
                            Icon(Icons.Filled.Star, contentDescription = null, tint = OrangeDark, modifier = Modifier.size(16.dp))
                        else
                            Icon(Icons.Outlined.StarBorder, contentDescription = null, tint = OrangeDark, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, tint = BlueDark, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${recipe.duration} min", fontFamily = Poppins, fontSize = 12.sp, color = BlueDark)
                    Spacer(Modifier.width(16.dp))
                    Icon(Icons.Filled.Person, contentDescription = null, tint = BlueDark, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${recipe.portions} porciones", fontFamily = Poppins, fontSize = 12.sp, color = BlueDark)
                }
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = BlueDark)
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar receta",
                        tint = Orange
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyRecipesPreview() {
    // NavController no es necesario para preview, pasamos un stub
    MyRecipesScreen(navController = rememberNavController())
}

data class RecipeData(
    val id: Int,
    val title: String,
    val imageRes: Int,
    val rating: Int,
    val duration: Int,
    val portions: Int
)

private fun sampleRecipes() = listOf(
    RecipeData(1, "Budín de limón y amapolas", R.drawable.img_pastas, 4, 70, 8),
    RecipeData(2, "Tarta de atún", R.drawable.img_snacks, 3, 50, 10),
    RecipeData(3, "Alfajores de chocolate", R.drawable.img_cheesecake, 5, 40, 12)
)
