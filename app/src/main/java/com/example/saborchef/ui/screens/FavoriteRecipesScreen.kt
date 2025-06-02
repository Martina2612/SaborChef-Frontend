package com.example.saborchef.ui.screens

import com.example.saborchef.ui.components.BottomBar
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saborchef.R
import com.example.saborchef.model.UserRole
import com.example.saborchef.ui.components.RecipeCard
import com.example.saborchef.ui.components.SearchBar
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Pantalla de recetas favoritas con swipe to delete, confirm dialog y popup de eliminado.
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoriteRecipesScreen(
    navController: NavController,
    role: UserRole,
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    showConfirmDialog: Boolean = false,
    showDeletedPopup: Boolean = false,
    onSwipeRecipe: (FavoriteRecipe) -> Unit = {},
    onConfirmDelete: () -> Unit = {},
    onCancelDelete: () -> Unit = {}
) {
    // Estado de la lista de recetas favoritas
    var recipes by remember {
        mutableStateOf(
            listOf(
                FavoriteRecipe(1, "Pollo al curry", "usuario1", R.drawable.img_spaghetti, 3),
                FavoriteRecipe(2, "Hamburguesa vegana", "usuario2", R.drawable.img_step1, 4),
                FavoriteRecipe(3, "Café latte", "usuario3", R.drawable.img_step2, 2),
                FavoriteRecipe(4, "Cheesecake", "usuario4", R.drawable.img_ratatouille, 5)
            )
        )
    }
    // Control de diálogo y popup
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showDeletedPopup by remember { mutableStateOf(false) }
    var recipeToDelete by remember { mutableStateOf<FavoriteRecipe?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Estado del modal bottom sheet
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    // Mostrar u ocultar sheet según flag
    LaunchedEffect(showConfirmDialog) {
        if (showConfirmDialog) bottomSheetState.show() else bottomSheetState.hide()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                title = {
                    Text(
                        text = "Recetas favoritas",
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
                }
            )
        },
        bottomBar = { BottomBar(navController, role) }
    ) {
        Column(Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recipes, key = { it.id }) { recipe ->
                    SwipeToDeleteRecipeItem(
                        recipe = recipe,
                        onSwipe = {
                            recipeToDelete = it
                            showConfirmDialog = true
                        }
                    )
                }
            }
        }

        // Modal bottom sheet para confirmación
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            sheetContent = {
                ConfirmDeletionSheet(
                    onConfirm = {
                        recipes = recipes.filterNot { it.id == recipeToDelete?.id }
                        showConfirmDialog = false
                        // mostrar popup de eliminado
                        showDeletedPopup = true
                        coroutineScope.launch {
                            delay(2000)
                            showDeletedPopup = false
                        }
                    },
                    onCancel = {
                        showConfirmDialog = false
                    }
                )
            }
        ) {
        }

        // Popup de receta eliminada
        AnimatedVisibility(visible = showDeletedPopup) {
            DeletedPopup()
        }
    }
}

/**
 * Item con swipe left para eliminar.
 */
@Composable
private fun SwipeToDeleteRecipeItem(
    recipe: FavoriteRecipe,
    onSwipe: (FavoriteRecipe) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    offsetX += dragAmount
                    if (offsetX < -100f) {
                        onSwipe(recipe)
                        offsetX = 0f
                    }
                }
            }
    ) {
        Spacer(Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(Orange, shape = RoundedCornerShape(16.dp))
                .padding(end = 16.dp)
        ) {
            Spacer(Modifier.weight(1f))
            Icon(imageVector=Icons.Default.DeleteOutline, contentDescription = "Eliminar", tint = Color.White)
        }

        RecipeCard(
            id = recipe.id.toString(),
            title = recipe.title,
            imageUrl = Uri.parse("android.resource://" + context.packageName + "/" + recipe.imageRes),
            duration = "--",
            portions = 1,
            rating = recipe.rating,
            user = recipe.user,
            onClick = {}
        )
    }
}

/**
 * Hoja de confirmación de borrado.
 */
@Composable
private fun ConfirmDeletionSheet(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "¿Está seguro que desea eliminar esta receta?",
            fontWeight = FontWeight.Bold,
            fontFamily = Poppins,
            color = BlueDark
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(backgroundColor = Orange),
            shape = RoundedCornerShape(50)
        ) {
            Text("Sí, eliminar", color = Color.White, fontFamily = Poppins)
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onCancel,
            shape = RoundedCornerShape(50)
        ) {
            Text("No, cancelar", fontFamily = Poppins)
        }
    }
}

/**
 * Popup de confirmación de receta eliminada.
 */
@Composable
private fun DeletedPopup() {
    Box(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = 10.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_wrong),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text("Receta eliminada!", fontWeight = FontWeight.Bold, fontFamily = Poppins, color = BlueDark)
            }
        }
    }
}

// Previews de diferentes estados
@Preview(showBackground = true)
@Composable
fun PreviewFavoriteRecipesDefault() {
    FavoriteRecipesScreen(
        navController = /* navController stub */ NavController(LocalContext.current),
        role = UserRole.USUARIO,
        query = "",
        onQueryChange = {},
        onFilterClick = {},
        showConfirmDialog = false,
        showDeletedPopup = false
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFavoriteRecipesConfirmSheet() {
    FavoriteRecipesScreen(
        navController = NavController(LocalContext.current),
        role = UserRole.USUARIO,
        query = "",
        onQueryChange = {},
        onFilterClick = {},
        showConfirmDialog = true,
        showDeletedPopup = false
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewFavoriteRecipesDeletedPopup() {
    FavoriteRecipesScreen(
        navController = NavController(LocalContext.current),
        role = UserRole.USUARIO,
        query = "",
        onQueryChange = {},
        onFilterClick = {},
        showConfirmDialog = false,
        showDeletedPopup = true
    )
}


/**
 * Modelo de datos para receta favorita.
 */
data class FavoriteRecipe(
    val id: Int,
    val title: String,
    val user: String,
    val imageRes: Int,
    val rating: Int
)
