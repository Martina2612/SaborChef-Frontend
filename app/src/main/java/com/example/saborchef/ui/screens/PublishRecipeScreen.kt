package com.example.saborchef.ui.publish

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.saborchef.models.IngredienteCantidad
import com.example.saborchef.ui.publish.PublishRecipeViewModel.PublishUiState
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.R
import com.example.saborchef.ui.components.PhotosCarousel
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults as texto


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishRecipeScreen(
    navController: NavController,

    ) {
    val viewModel: PublishRecipeViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Form fields
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf(30f) }
    var porciones by remember { mutableStateOf(1) }
    var tipo by remember { mutableStateOf("") }
    var ingredientes by remember { mutableStateOf(mutableStateListOf<IngredienteCantidad>()) }
    var mainPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    //Launcher para fotos
    val pickMain = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> mainPhotos = (mainPhotos + uris).take(10) }

    // Duplicate dialog state
    var showDuplicateDialog by remember { mutableStateOf(false) }
    // Success dialog state
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ERRORES
    var showNombreError by remember { mutableStateOf(false) }
    var showDescripcionError by remember { mutableStateOf(false) }
    var showCategoriaError by remember { mutableStateOf(false) }
    var showFotosError by remember { mutableStateOf(false) }
    var showIngredienteError by remember { mutableStateOf(false) }
    var showPasoError by remember { mutableStateOf(false) }

    // Observa cambios de estado UI para mostrar diálogos
    LaunchedEffect(uiState) {
        when (uiState) {
            PublishRecipeViewModel.PublishUiState.Loading -> {/* opcional: indicador */}
            PublishRecipeViewModel.PublishUiState.Duplicate -> showDuplicateDialog = true
            PublishRecipeViewModel.PublishUiState.Success -> showSuccessDialog = true
            is PublishRecipeViewModel.PublishUiState.Error -> errorMessage = (uiState as PublishRecipeViewModel.PublishUiState.Error).message
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("", fontFamily = Poppins, fontWeight=FontWeight.Bold, color= BlueDark) },
                navigationIcon = {
                    Text(
                        text = "Cancelar",
                        color = OrangeDark,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(horizontal = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            PhotosCarousel(
                photos    = mainPhotos,
                onRemove  = { idx -> mainPhotos = mainPhotos.filterIndexed { i, _ -> i != idx } },
                onAddClick= { pickMain.launch("image/* video/*") }
            )
            if (showFotosError && mainPhotos.isEmpty()) Text("Debe subir al menos una imagen", color = Color.Red, fontSize = 12.sp)

            Spacer(Modifier.height(24.dp))
            Text("Nombre de la receta", fontWeight = FontWeight.SemiBold, color=BlueDark, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            // Nombre y descripción
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Ingresa el nombre de tu receta", fontSize=14.sp) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color(0xFFD0D8E8),
                    unfocusedIndicatorColor = Color(0xFFD0D8E8),
                    cursorColor = BlueDark,
                    placeholderColor = Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            if (showNombreError && nombre.isBlank()) Text("Campo obligatorio", color = Color.Red, fontSize = 12.sp)

            Spacer(Modifier.height(24.dp))
            Text("Descripción", fontWeight = FontWeight.SemiBold, color=BlueDark, fontSize = 18.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Cuéntanos un poco acerca de tu receta", fontSize=14.sp) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    focusedIndicatorColor = Color(0xFFD0D8E8),
                    unfocusedIndicatorColor = Color(0xFFD0D8E8),
                    cursorColor = BlueDark,
                    placeholderColor = Color.Gray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            if (showDescripcionError && descripcion.isBlank()) Text("Campo obligatorio", color = Color.Red, fontSize = 12.sp)

            Spacer(Modifier.height(24.dp))

            // Duración de la receta (en minutos)
            Text(
                text = "Duración de la receta (en minutos)",
                fontWeight = FontWeight.SemiBold,
                color = BlueDark,
                fontSize = 18.sp,
            )
            val labels = listOf("10", "30", "45", "60", "80", "100", "120")
            val sliderPositions = listOf(0f, 30f, 45f, 60f, 80f, 100f, 120f)

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sliderPositions.forEachIndexed { index, value ->
                    Text(
                        text = labels[index],
                        color = if (duracion >= value) OrangeDark else Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Slider(
                value = duracion,
                onValueChange = { duracion = it },
                valueRange = 0f..120f,
                steps = 5,
                colors = SliderDefaults.colors(
                    activeTrackColor = OrangeDark,
                    inactiveTrackColor = Color.LightGray,
                    thumbColor = OrangeDark
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))


            // Número de raciones
            Text(
                text = "Número de raciones",
                fontWeight = FontWeight.SemiBold,
                color = BlueDark,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(16.dp))
            // Contenedor con borde y fondo
            Box(
                modifier = Modifier
                    .padding(horizontal=16.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFFD0D8E8),            // Color del borde
                        shape = RoundedCornerShape(8.dp)      // Misma forma redondeada
                    )
                    .background(
                        color = Color.White,           // Fondo gris claro
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    IconButton(
                        onClick = { if (porciones > 1) porciones-- },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = BlueDark)
                    }
                    Text(
                        text = porciones.toString(),
                        fontSize = 16.sp,
                        color = BlueDark,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(
                        onClick = { porciones++ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = BlueDark)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            // Categoría
            Text(
                text = "Categoría",
                fontWeight = FontWeight.SemiBold,
                color = BlueDark,
                fontSize = 18.sp,
                fontFamily = Poppins
            )
            var tipoExpanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedButton(
                    onClick = { tipoExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFD0D8E8)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFF5F5F5),
                        contentColor = BlueDark
                    )
                ) {
                    Text(
                        text = tipo.ifEmpty { "Selecciona un tipo" },
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BlueDark)
                }
                DropdownMenu(
                    expanded = tipoExpanded,
                    onDismissRequest = { tipoExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    listOf(
                        "Snacks",
                        "Postres",
                        "Vegano",
                        "Carnes",
                        "Bebidas",
                        "Pastas",
                        "Vegetariano",
                        "Tartas",
                        "Ensaladas",
                        "Sopa"
                    ).forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = BlueDark, fontFamily = Poppins) },
                            onClick = {
                                tipo = option
                                tipoExpanded = false
                            }
                        )
                    }
                }
            }
            if (showCategoriaError && tipo.isBlank()) Text("Debe seleccionar una categoría", color = Color.Red, fontSize = 12.sp)

            Spacer(Modifier.height(24.dp))

            // Ingredientes
            IngredientsSection(
                ingredientes = ingredientes,
                onRemove = { i -> ingredientes.removeAt(i) },
                onAdd    = { ingredientes.add(IngredienteCantidad()) }
            )
            if (showIngredienteError && ingredientes.none { it.nombreIngrediente?.isNotBlank() == true }) Text("Debe agregar al menos un ingrediente", color = Color.Red, fontSize = 12.sp)

            Spacer(Modifier.height(24.dp))

            // Pasos

            StepsSection(
                steps            = viewModel.steps,
                onAddStep        = { viewModel.steps.add(StepItem()) },
                onUpdateDescription = { idx, text -> viewModel.updateStepDescription(idx, text) },
                onAddMedia       = { idx, uris -> viewModel.updateStepMedia(idx, (viewModel.steps[idx].media + uris).take(6)) },
                onRemoveMedia    = { idx, mediaIdx ->
                    val current = viewModel.steps[idx].media.toMutableList()
                    current.removeAt(mediaIdx)
                    viewModel.updateStepMedia(idx, current)
                }
            )
            if (showPasoError && viewModel.steps.none { it.description.isNotBlank() }) Text("Debe agregar al menos un paso", color = Color.Red, fontSize = 12.sp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón "Volver"
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BlueDark),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = BlueDark
                    )
                ) {
                    Text("Volver", fontSize = 16.sp)
                }

                Spacer(Modifier.width(12.dp))

                // Botón "Publicar"
                Button(onClick = {
                    showNombreError = nombre.isBlank()
                    showDescripcionError = descripcion.isBlank()
                    showCategoriaError = tipo.isBlank()
                    showFotosError = mainPhotos.isEmpty()
                    showIngredienteError = ingredientes.none { it.nombreIngrediente?.isNotBlank() == true }
                    showPasoError = viewModel.steps.none { it.description.isNotBlank() }

                    val isValid = listOf(
                        showNombreError,
                        showDescripcionError,
                        showCategoriaError,
                        showFotosError,
                        showIngredienteError,
                        showPasoError
                    ).none { it }

                    if (isValid) {
                        viewModel.submitRecipe(
                            photos = mainPhotos,
                            nombre = nombre,
                            descripcion = descripcion,
                            duracion = duracion.toInt(),
                            porciones = porciones,
                            tipo = tipo,
                            ingredientes = ingredientes.toList()
                        )
                    }
                },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeDark,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (uiState is PublishUiState.Loading) "Publicando..." else "Publicar",color=Color.White,
                        fontSize = 16.sp
                    )
                }
            }

        }
    }

    if (showDuplicateDialog) {
        Dialog(onDismissRequest = {
            showDuplicateDialog = false
            viewModel.resetState()
        }) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(color = Color(0x1AFF0000), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ya tienes una receta con ese nombre",
                        fontWeight = FontWeight.Bold,
                        color = BlueDark,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "¿Desea reemplazarla?",
                        fontSize = 14.sp,
                        color = BlueDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                showDuplicateDialog = false
                                viewModel.resetState()
                            },
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Text("No, regresar", color = BlueDark)
                        }

                        Button(
                            onClick = {
                                showDuplicateDialog = false
                                viewModel.confirmReplace()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeDark)
                        ) {
                            Text("Sí, reemplazar", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Imagen ilustrativa
                    Image(
                        painter = painterResource(id = R.drawable.chef_popup),
                        contentDescription = null,
                        modifier = Modifier
                            .height(150.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Receta añadida!",
                        fontWeight = FontWeight.Bold,
                        color = BlueDark, // Azul oscuro
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Actualmente se encuentra pendiente de validación por la empresa. Podrás encontrarla por ahora en “Mis Recetas”.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.resetState()
                            navController.navigate("simple_home")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeDark) // Naranja
                    ) {
                        Text("Volver al home", color = Color.White)
                    }
                }
            }
        }
    }
    // Error AlertDialog remains the same
    errorMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = {
                errorMessage = null
                viewModel.resetState()
            },
            title = { Text("Error") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = {
                    errorMessage = null
                    viewModel.resetState()
                }) { Text("OK") }
            }
        )
    }
}

@Composable
fun IngredientsSection(
    ingredientes: SnapshotStateList<IngredienteCantidad>,
    onRemove: (Int) -> Unit,
    onAdd: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Ingredientes",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = BlueDark,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ingredientes.forEachIndexed { idx, ing ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(56.dp)

            ) {
                // Nombre del ingrediente
                OutlinedTextField(
                    value = ing.nombreIngrediente ?: "",
                    onValueChange = { ingredientes[idx] = ing.copy(nombreIngrediente = it) },
                    placeholder = {
                        Text("Nombre", color = Color.Gray, fontSize = 12.sp)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color(0xFFD0D8E8),
                        unfocusedIndicatorColor = Color(0xFFD0D8E8),
                        cursorColor = BlueDark,
                        placeholderColor = Color.Gray
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Cantidad
                OutlinedTextField(
                    value = if (ing.cantidad == 0f) "" else ing.cantidad.toString(),
                    onValueChange = {
                        ingredientes[idx] = ing.copy(cantidad = it.toFloatOrNull() ?: 0f)
                    },
                    placeholder = {
                        Text("Cant.", color = Color.Gray, fontSize = 12.sp)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color(0xFFD0D8E8),
                        unfocusedIndicatorColor = Color(0xFFD0D8E8),
                        cursorColor = BlueDark,
                        placeholderColor = Color.Gray
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Unidad (Dropdown)
                var expand by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    OutlinedButton(
                        onClick = { expand = true },
                        modifier = Modifier.fillMaxSize(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFD0D8E8)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = BlueDark
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = ing.unidad ?: "unid.",
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = BlueDark
                        )
                    }

                    DropdownMenu(
                        expanded = expand,
                        onDismissRequest = { expand = false },
                        modifier = Modifier
                            .width(90.dp)
                            .background(Color.White)
                    ) {
                        listOf("gr", "kg", "ml", "l", "unid.").forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u, color = BlueDark) },
                                onClick = {
                                    ingredientes[idx] = ing.copy(unidad = u)
                                    expand = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Eliminar ingrediente
                IconButton(
                    onClick = { onRemove(idx) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFF5F5F5), CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = OrangeDark
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón: + Ingrediente
        Button(
            onClick = onAdd,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, BlueDark),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = BlueDark
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = BlueDark)
            Spacer(Modifier.width(8.dp))
            Text("Ingrediente", fontSize = 16.sp)
        }
    }
}



@Composable
fun StepsSection(
    steps: SnapshotStateList<StepItem>,
    onAddStep: () -> Unit,
    onUpdateDescription: (Int, String) -> Unit,
    onAddMedia: (Int, List<Uri>) -> Unit,
    onRemoveMedia: (Int, Int) -> Unit
) {
    Text("Paso a paso", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = BlueDark)
    Spacer(Modifier.height(8.dp))

    steps.forEachIndexed { idx, step ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(1.dp, Color(0xFFD0D8E8), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(BlueDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${idx+1}", color = Color.White, fontSize = 14.sp)
                }
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = step.description,
                    onValueChange = { onUpdateDescription(idx, it) },
                    placeholder = { Text("Describe el paso") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor        = Color.White,
                        focusedIndicatorColor   = BlueDark,
                        unfocusedIndicatorColor = Color(0xFFD0D8E8),
                        cursorColor            = BlueDark,
                        placeholderColor       = Color(0xFFB0B6C3),
                        textColor              = BlueDark
                    ),
                    singleLine = false,
                    shape = RoundedCornerShape(0.dp)
                )
                Spacer(Modifier.width(4.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Media carousel
            val pagerState = rememberPagerState(pageCount = { step.media.size })
            if (step.media.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) { page ->
                    Box {
                        AsyncImage(
                            model = step.media[page],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                        )
                        IconButton(
                            onClick = { onRemoveMedia(idx, page) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color.White.copy(alpha = 0.7f), CircleShape)
                                .size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.Gray)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                // indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(step.media.size) { page ->
                        Box(
                            Modifier
                                .size(if (page == pagerState.currentPage) 8.dp else 6.dp)
                                .padding(2.dp)
                                .background(
                                    if (page == pagerState.currentPage) BlueDark else Color.LightGray,
                                    CircleShape
                                )
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Botón añadir media
            val pickMedia = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetMultipleContents()
            ) { uris -> onAddMedia(idx, uris) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .border(
                        width = 1.dp,
                        color = BlueDark,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { pickMedia.launch("image/* video/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Agregar media", tint = Color.Gray)
            }
        }
    }

    Spacer(Modifier.height(8.dp))
    Button(
        onClick = onAddStep,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = BlueDark
        ),
        border = BorderStroke(1.dp, BlueDark),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(Modifier.width(4.dp))
        Text("Paso")
    }
}