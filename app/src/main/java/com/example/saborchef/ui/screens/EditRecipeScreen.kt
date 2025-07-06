package com.example.saborchef.ui.screens

import android.app.Application
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.models.IngredienteCantidad
import com.example.saborchef.models.IngredienteDetalle
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.models.PasoDetalle
import com.example.saborchef.ui.components.PhotosCarousel
import com.example.saborchef.ui.publish.PublishRecipeViewModel
import com.example.saborchef.viewmodel.PublishRecipeViewModelFactory
import com.example.saborchef.ui.publish.StepItem
import com.example.saborchef.ui.publish.IngredientsSection
import com.example.saborchef.ui.publish.StepsSection
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    navController: NavController,
    receta: RecetaDetalleResponse
) {
    val context = LocalContext.current
    // Crear ViewModel usando la fábrica proporcionada en ui.publish
    val viewModel: PublishRecipeViewModel = viewModel(
        factory = PublishRecipeViewModelFactory(context.applicationContext as Application)
    )

    // Inicializar estados con datos de la receta
    var nombre by remember { mutableStateOf(receta.nombre.orEmpty()) }
    var descripcion by remember { mutableStateOf(receta.descripcion.orEmpty()) }
    var duracion by remember { mutableStateOf(receta.duracion?.toFloat() ?: 30f) }
    var porciones by remember { mutableStateOf(receta.porciones ?: 1) }
    var tipo by remember { mutableStateOf(receta.tipo.orEmpty()) }

    // Mapear IngredienteDetalle a IngredienteCantidad
    val initialIngr: List<IngredienteCantidad> = receta.ingredientes
        ?.map { det: IngredienteDetalle ->
            IngredienteCantidad(
                nombreIngrediente = det.nombre,
                cantidad = det.cantidad?.toFloat() ?: 0f,
                unidad = det.unidad
            )
        } ?: emptyList()
    var ingredientes = remember { mutableStateListOf<IngredienteCantidad>().apply { addAll(initialIngr) } }

    // Mapear fotos (lista de URLs) a Uri
    val initialPhotos: List<Uri> = receta.fotos
        ?.mapNotNull { uriString -> runCatching { Uri.parse(uriString) }.getOrNull() }
        ?: emptyList()
    var mainPhotos by remember { mutableStateOf(initialPhotos) }

    // Selector para nuevas fotos
    val pickMain = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> mainPhotos = (mainPhotos + uris).take(10) }

    // Estados para los diálogos
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsState()

    // Observar cambios de estado en el ViewModel
    LaunchedEffect(uiState) {
        when (uiState) {
            is PublishRecipeViewModel.PublishUiState.Duplicate -> showDuplicateDialog = true
            is PublishRecipeViewModel.PublishUiState.Success -> showSuccessDialog = true
            is PublishRecipeViewModel.PublishUiState.Error -> errorMessage = (uiState as PublishRecipeViewModel.PublishUiState.Error).message
            else -> Unit
        }
    }

    // Cargar pasos iniciales desde RecetaDetalleResponse
    LaunchedEffect(Unit) {
        val pasosList: List<StepItem> = receta.pasos
            ?.map { paso: PasoDetalle ->
                StepItem(
                    description = paso.texto.orEmpty(),
                    media = paso.contenidos
                        ?.mapNotNull { m -> runCatching { Uri.parse(m.url) }.getOrNull() }
                        ?: emptyList()
                )
            } ?: listOf(StepItem())
        viewModel.steps.clear()
        viewModel.steps.addAll(pasosList)
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Receta", color = BlueDark) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Cancelar", color = OrangeDark)
                    }
                }
            )
        }
    ) { paddingVals ->
        Column(
            Modifier
                .verticalScroll(scrollState)
                .padding(paddingVals)
                .padding(16.dp)
        ) {
            PhotosCarousel(
                photos = mainPhotos,
                onRemove = { idx -> mainPhotos = mainPhotos.filterIndexed { i, _ -> i != idx } },
                onAddClick = { pickMain.launch("image/* video/*") }
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            Text("Duración: ${duracion.toInt()} minutos")
            Slider(
                value = duracion,
                onValueChange = { duracion = it },
                valueRange = 0f..120f
            )
            Spacer(Modifier.height(24.dp))

            Text("Porciones: $porciones")
            Row {
                Button(onClick = { if (porciones > 1) porciones-- }) { Text("-") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { porciones++ }) { Text("+") }
            }
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            IngredientsSection(
                ingredientes = ingredientes,
                onRemove = { i -> ingredientes.removeAt(i) },
                onAdd = { ingredientes.add(IngredienteCantidad()) }
            )
            Spacer(Modifier.height(24.dp))

            StepsSection(
                steps = viewModel.steps,
                onAddStep = { viewModel.steps.add(StepItem()) },
                onUpdateDescription = viewModel::updateStepDescription,
                onAddMedia = viewModel::updateStepMedia,
                onRemoveMedia = { sIdx, mIdx ->
                    val current = viewModel.steps[sIdx].media.toMutableList()
                    current.removeAt(mIdx)
                    viewModel.updateStepMedia(sIdx, current)
                }
            )
            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                viewModel.submitRecipe(
                    photos = mainPhotos,
                    nombre = nombre,
                    descripcion = descripcion,
                    duracion = duracion.toInt(),
                    porciones = porciones,
                    tipo = tipo,
                    ingredientes = ingredientes.toList()
                )
            }) {
                Text("Guardar cambios")
            }
        }
    }

    // Diálogo duplicado
    if (showDuplicateDialog) {
        AlertDialog(
            onDismissRequest = {
                showDuplicateDialog = false
                viewModel.resetState()
            },
            title = { Text("Receta duplicada") },
            text = { Text("Ya tienes una receta con ese nombre. ¿Deseas reemplazarla?") },
            confirmButton = {
                TextButton(onClick = {
                    showDuplicateDialog = false
                    viewModel.confirmReplace()
                }) { Text("Reemplazar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDuplicateDialog = false
                    viewModel.resetState()
                }) { Text("Cancelar") }
            }
        )
    }

    // Diálogo éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                navController.popBackStack()
            },
            title = { Text("Éxito") },
            text = { Text("Receta editada correctamente.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navController.popBackStack()
                }) { Text("OK") }
            }
        )
    }

    // Diálogo error
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
