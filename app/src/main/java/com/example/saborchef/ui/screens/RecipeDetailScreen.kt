package com.example.saborchef.ui.screens

import android.app.Application
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.viewmodel.RecipeDetailUiState
import com.example.saborchef.viewmodel.RecipeDetailViewModel
import com.google.accompanist.pager.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.Button
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.viewmodel.CalificacionViewModel
import com.example.saborchef.viewmodel.FavoritesViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.viewmodel.ComentariosViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalContext
import coil.imageLoader
import coil.request.ImageRequest
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.ui.components.Base64Image
import com.example.saborchef.ui.components.PortionSelector
import com.example.saborchef.ui.components.ScaledIngredientsDisplay
import com.example.saborchef.viewmodel.ScaledRecipesViewModel
import com.example.saborchef.viewmodel.ScaledRecipeUiState
import com.example.saborchef.viewmodel.SaveState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    navController: NavController,
    onBack: () -> Unit = {}
) {
    val id = recipeId.toLongOrNull() ?: return
    val viewModel: RecipeDetailViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return RecipeDetailViewModel(id) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is RecipeDetailUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BlueDark)
            }
        }
        is RecipeDetailUiState.Error -> {
            val msg = (uiState as RecipeDetailUiState.Error).message
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(msg, color = Color.Red)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchRecipeDetail() }) {
                        Text("Reintentar")
                    }
                }
            }
        }
        is RecipeDetailUiState.Success -> {
            val recipe = (uiState as RecipeDetailUiState.Success).recipe
            RecipeDetailContent(
                recipe = recipe,
                navController = navController,
                onBack = onBack
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeDetailContent(
    recipe: RecetaDetalleResponse,
    navController: NavController,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val dataStore = DataStoreManager(context)
    val role by dataStore.role.collectAsState(initial = "")
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val favorites by favoritesViewModel.favorites.collectAsState(initial = emptyList())

    // ✅ NUEVO: ViewModel para recetas escaladas
    val scaledRecipesViewModel: ScaledRecipesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ScaledRecipesViewModel(context.applicationContext as Application) as T
            }
        }
    )

    // ✅ NUEVO: Estados del escalado
    val scaledRecipeState by scaledRecipesViewModel.scaledRecipeState.collectAsState()
    val saveState by scaledRecipesViewModel.saveState.collectAsState()
    val savedCount by scaledRecipesViewModel.savedCount.collectAsState()
    var selectedPortions by remember { mutableStateOf(recipe.porciones ?: 1) }

    LaunchedEffect(role) {
        Log.d("RecipeDetail", "Rol actual: $role")
    }

    val isFav = favorites.any { it.idReceta == recipe.idReceta }

    val califVm: CalificacionViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CalificacionViewModel(recipe.idReceta!!, context.applicationContext as Application) as T
            }
        }
    )
    val promedio by califVm.promedio.collectAsState()
    val selected by califVm.userRating.collectAsState()

    val comentariosVM: ComentariosViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ComentariosViewModel(context.applicationContext as Application) as T
            }
        }
    )

    val comentariosList by comentariosVM.comentarios.collectAsState()
    var nuevoTexto by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(recipe.idReceta) {
        favoritesViewModel.loadFavorites()
        califVm.loadPromedio(recipe.idReceta!!)
        califVm.loadUserRating(recipe.idReceta!!)
        comentariosVM.loadComentarios(recipe.idReceta!!)
    }

    val fotos = recipe.fotos.orEmpty()
    val ingredientes = recipe.ingredientes.orEmpty()
    val pasos = recipe.pasos.orEmpty()
    val comentarios = recipe.comentarios.orEmpty()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(innerPadding)
        ) {
            val pagerState = rememberPagerState()
            Box {
                HorizontalPager(
                    count = fotos.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) { page ->
                    val context = LocalContext.current
                    val base64Image = fotos[page]
                    val dataUrl = "data:image/jpeg;base64,$base64Image"

                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(dataUrl)
                            .build(),
                        imageLoader = context.imageLoader
                    )

                    Base64Image(
                        base64String = fotos[page],
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                ) {
                    repeat(fotos.size) { idx ->
                        val color = if (idx == pagerState.currentPage) BlueDark else Color.LightGray
                        Box(
                            Modifier
                                .size(8.dp)
                                .padding(4.dp)
                                .background(color, shape = CircleShape)
                        )
                    }
                }
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(36.dp)
                        .background(Color.White, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = BlueDark
                    )
                }
                LaunchedEffect(isFav, role) {
                    Log.d("RecipeDetail", "isFav=$isFav, mostrar botón? ${role == "USUARIO" || role == "ALUMNO"}")
                }
                if (role == "USUARIO" || role == "ALUMNO") {
                    IconButton(
                        onClick = {
                            if (isFav) favoritesViewModel.removeFavorite(recipe.idReceta)
                            else favoritesViewModel.addFavorite(recipe.idReceta)
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Favorito",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Bloque con fondo más oscuro para título, usuario, estrellas, duración y porciones
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFECECEC)) // fondo gris más oscuro
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = recipe.nombre.orEmpty(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    // Fila: usuario (izq) y estrellas (der)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recipe.nombreUsuario.orEmpty(),
                            fontSize = 16.sp,
                            color = BlueDark
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val avg = promedio?.roundToInt() ?: 0
                            repeat(5) { idx ->
                                Icon(
                                    imageVector = if (idx < avg) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = null,
                                    tint = OrangeDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // REEMPLAZO: Fila de duración y porciones con selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏱ ${recipe.duracion} minutos",
                            fontSize = 16.sp,
                            color = BlueDark
                        )

                        // ✅ NUEVO: Selector de porciones clickeable
                        PortionSelector(
                            currentPortions = selectedPortions,
                            onPortionsSelected = { newPortions ->
                                selectedPortions = newPortions
                                recipe.idReceta?.let { recipeId ->
                                    scaledRecipesViewModel.scaleRecipeByPortions(recipeId, newPortions)
                                }
                            }
                        )
                    }
                }
            }

            // ✅ NUEVO: Mostrar receta escalada si existe
            when (scaledRecipeState) {
                is ScaledRecipeUiState.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = OrangeDark,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Calculando ingredientes...",
                                color = BlueDark,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                is ScaledRecipeUiState.Success -> {
                    ScaledIngredientsDisplay(
                        scaledRecipe = (scaledRecipeState as ScaledRecipeUiState.Success).scaledRecipe,
                        saveState = saveState,
                        canSaveMore = scaledRecipesViewModel.canSaveMore(),
                        savedCount = savedCount,
                        onSaveRecipe = {
                            recipe.idReceta?.let { recipeId ->
                                scaledRecipesViewModel.saveScaledRecipe(recipeId, selectedPortions)
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }

                is ScaledRecipeUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                    ) {
                        Text(
                            text = "Error: ${(scaledRecipeState as ScaledRecipeUiState.Error).message}",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                else -> {
                    // Estado inicial - no mostrar nada
                }
            }

            // ✅ Manejar efectos de guardado
            LaunchedEffect(saveState) {
                when (saveState) {
                    is SaveState.Success -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar((saveState as SaveState.Success).message)
                            scaledRecipesViewModel.resetSaveState()
                        }
                    }
                    is SaveState.Error -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Error: ${(saveState as SaveState.Error).message}")
                            scaledRecipesViewModel.resetSaveState()
                        }
                    }
                    else -> {}
                }
            }

            // Título "Descripción" antes del texto
            Text(
                "Descripción",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = BlueDark,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Text(
                text = recipe.descripcion.orEmpty(),
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                color = BlueDark,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )

            // Ingredientes
            Text(
                "Ingredientes",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = BlueDark,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            ingredientes.forEach { ing ->
                val texto = listOfNotNull(ing.nombre, ing.cantidad?.toString(), ing.unidad)
                    .joinToString(" ")
                Text(
                    "• $texto",
                    fontSize = 16.sp,
                    color = BlueDark,
                    modifier = Modifier.padding(start = 24.dp, top = 4.dp)
                )
            }

            // Preparación
            Text(
                "Preparación",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = BlueDark,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            pasos.forEachIndexed { index, paso ->
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .background(BlueDark, shape = CircleShape)
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = paso.texto.orEmpty(),
                            fontSize = 16.sp,
                            color = BlueDark
                        )

                        val contenidos = paso.contenidos.orEmpty()
                        if (contenidos.isNotEmpty()) {
                            val stepPager = rememberPagerState()
                            HorizontalPager(
                                count = contenidos.size,
                                state = stepPager,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(top = 8.dp)
                            ) { page ->
                                Base64Image(
                                    base64String = contenidos[page].url.orEmpty(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                repeat(contenidos.size) { idx2 ->
                                    val dotColor = if (idx2 == stepPager.currentPage) BlueDark else Color.LightGray
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .padding(2.dp)
                                            .background(dotColor, shape = CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (role == "USUARIO" || role == "ALUMNO") {
                Text(
                    "Califica la receta",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = BlueDark,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    Modifier.padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { idx ->
                        Icon(
                            imageVector = if (idx < selected) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = OrangeDark,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    califVm.calificar(recipe.idReceta!!, idx + 1)
                                }
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = promedio?.let { String.format("(%.1f)", it) } ?: "",
                        fontSize = 16.sp,
                        color = BlueDark
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Comentarios",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = BlueDark,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            comentariosList.forEach { c ->
                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(c.nombreUsuario.toString(), fontWeight = FontWeight.Medium, color = BlueDark)
                    Text(c.texto.toString(), fontSize = 16.sp, color = Color.DarkGray)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Input + botón de enviar sólo para USUARIO/ALUMNO
            if (role == "USUARIO" || role == "ALUMNO") {
                var nuevoTexto by rememberSaveable { mutableStateOf("") }

                OutlinedTextField(
                    value = nuevoTexto,
                    onValueChange = { nuevoTexto = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Añadí un comentario") },
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (nuevoTexto.isNotBlank()) {
                                comentariosVM.enviarComentario(recipe.idReceta!!, nuevoTexto) {
                                    nuevoTexto = ""
                                    coroutineScope.launch {
                                        snackbarHostState
                                            .showSnackbar("Tu comentario se enviará para aprobación")
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (nuevoTexto.isNotBlank()) {
                                comentariosVM.enviarComentario(recipe.idReceta!!, nuevoTexto) {
                                    nuevoTexto = ""
                                    coroutineScope.launch {
                                        snackbarHostState
                                            .showSnackbar("Tu comentario se enviará para aprobación")
                                        scrollState.animateScrollTo(scrollState.maxValue)
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar comentario"
                            )
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}