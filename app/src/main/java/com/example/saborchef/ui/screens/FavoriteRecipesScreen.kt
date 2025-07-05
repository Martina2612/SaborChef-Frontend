package com.example.saborchef.ui.screens

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.R
import com.example.saborchef.model.Rol
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.ui.components.RecipeCard
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.FavoritesViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRecipesScreen(
    navController: NavController,
    role: Rol
) {
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val favorites by favoritesViewModel.favorites.collectAsState(initial = emptyList())

    DisposableEffect(Unit) {
        val entry = navController.getBackStackEntry("favs")
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                favoritesViewModel.loadFavorites()
            }
        }
        entry.lifecycle.addObserver(observer)

        onDispose {
            entry.lifecycle.removeObserver(observer)
        }
    }

    var showConfirm by remember { mutableStateOf(false) }
    var showDeleted by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<RecetaDetalleResponse?>(null) }

    val deletePopupDuration = 2400
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Hide the deleted popup after a delay
    LaunchedEffect(showDeleted) {
        if (showDeleted) {
            delay(1500)
            showDeleted = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Recetas favoritas",
                        color = BlueDark,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("simple_home") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = BlueDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.White)
            )
        },
        bottomBar = { BottomBar(navController, role) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Lista de favoritas
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites, key = { it.idReceta ?: 0L }) { receta ->
                    SwipeToDeleteItem(
                        receta = receta,
                        onClick = { navController.navigate("recipe/${receta.idReceta}") },
                        onSwiped = {
                            toDelete = it
                            showConfirm = true
                        }
                    )
                }
                if (favorites.isEmpty()) {
                    item {
                        Text(
                            "No tienes recetas favoritas aún.",
                            color = BlueLight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // 1) Bottom sheet de confirmación (estilo mockup)
            if (showConfirm) {
                ModalBottomSheet(
                    onDismissRequest = { showConfirm = false },
                    sheetState = sheetState,
                    sheetMaxWidth = BottomSheetDefaults.SheetMaxWidth,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    containerColor = Color.White,
                    contentColor = contentColorFor(Color.White),
                    tonalElevation = 0.dp,
                    scrimColor = BottomSheetDefaults.ScrimColor,
                    contentWindowInsets = { BottomSheetDefaults.windowInsets },
                    properties = ModalBottomSheetDefaults.properties()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "¿Está seguro que desea eliminar esta receta?",
                            style = MaterialTheme.typography.titleMedium,
                            color = BlueDark,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                toDelete?.let { favoritesViewModel.removeFavorite(it.idReceta) }
                                showConfirm = false
                                showDeleted = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeDark),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(44.dp)
                        ) {
                            Text("Sí, eliminar", color = Color.White)
                        }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showConfirm = false },
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(44.dp)
                        ) {
                            Text("No, cancelar", color = BlueDark)
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            // 2) Popup “Receta eliminada!” centrado con animación y duración configurable
            AnimatedVisibility(
                visible = showDeleted,
                enter = scaleIn(
                    animationSpec = tween(durationMillis = 300, delayMillis = 0),
                    initialScale = 0.5f
                ) + fadeIn(animationSpec = tween(300)),
                exit = scaleOut(
                    animationSpec = tween(durationMillis = 300),
                    targetScale = 0.5f
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .width(260.dp)
                            .height(260.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.chef_popup),
                                contentDescription = "Receta eliminada",
                                modifier = Modifier.size(120.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Receta eliminada!",
                                style = MaterialTheme.typography.titleMedium,
                                color = BlueDark,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 3) Ocultar el popup después de X ms
            LaunchedEffect(showDeleted) {
                if (showDeleted) {
                    delay(deletePopupDuration.toLong())
                    showDeleted = false
                }
            }
        }
    }
}

@Composable
private fun SwipeToDeleteItem(
    receta: RecetaDetalleResponse,
    onClick: () -> Unit,
    onSwiped: (RecetaDetalleResponse) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val thresholdPx = with(density) { 100.dp.toPx() }

    Box(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX = (offsetX + dragAmount).coerceIn(-thresholdPx, 0f)
                    },
                    onDragEnd = {
                        if (offsetX <= -thresholdPx) {
                            // Se superó el umbral: llamamos al callback de swipe
                            onSwiped(receta)
                        }
                        // Siempre volvemos al estado inicial
                        offsetX = 0f
                    }
                )
            }
    ) {
        // Fondo naranja con icono (solo estético ahora)
        Box(
            Modifier
                .matchParentSize()
                .background(OrangeDark, shape = RoundedCornerShape(16.dp))
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                Icons.Default.DeleteOutline,
                contentDescription = "Eliminar",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        // Tarjeta desplazable
        Box(Modifier.offset { IntOffset(offsetX.roundToInt(), 0) }) {
            RecipeCard(
                id = receta.idReceta.toString(),
                title = receta.nombre.orEmpty(),
                imageUrl = Uri.parse(receta.fotoPrincipal),
                duration = "${receta.duracion} min",
                portions = receta.porciones ?: 1,
                rating = receta.promedioCalificacion?.toInt() ?: 0,
                user = receta.nombreUsuario.orEmpty()
            ) {
                onClick()
            }
        }
    }
}

