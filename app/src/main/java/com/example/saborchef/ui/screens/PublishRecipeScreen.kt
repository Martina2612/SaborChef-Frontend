package com.example.saborchef.ui.screens// file: PublishRecipeScreen.kt
/*package com.example.saborchef.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.saborchef.ui.theme.GrayExtraLight
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.PublishRecipeViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.rememberReorderableLazyList
import org.burnoutcrew.reorderable.reorderable
import org.burnoutcrew.reorderable.detectReorderAfterLongPress


// --- Data Models ---

data class IngredientItem(var name: String = "", var quantity: String = "", var unit: String = "unid.")
data class StepItem(var description: String = "", var media: Uri? = null)
data class PublishRecipeData(
    val photos: List<Uri>,
    val name: String,
    val description: String,
    val duration: Int,
    val servings: Int,
    val ingredients: List<IngredientItem>,
    val steps: List<StepItem>
)

// --- View State & Popups ---

enum class PublishResult { NONE, SUCCESS, DUPLICATE }

@Composable
fun PublishRecipeScreen(
    navController: NavController,
    viewModel: PublishRecipeViewModel,
) {
    val uiState by viewModel.submitState.collectAsState()
    val publishResult by viewModel.publishResult.collectAsState()

    // Local UI state
    var selectedPhotos by remember { mutableStateOf(mutableListOf<Uri>()) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            selectedPhotos.add(uri)
        }
    }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf(30f) }
    val durationMarks = listOf(10f, 30f, 45f, 60f, 80f, 100f, 120f)
    var servings by remember { mutableStateOf(1) }

    // Ingredients + reorder
    val ingredients = rememberReorderableLazyList(
        onMove = { from, to ->
            ingredientsList = ingredientsList.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
        }
    )
    var ingredientsList by remember { mutableStateOf(mutableListOf(IngredientItem())) }

    // Steps
    var stepsList by remember { mutableStateOf(mutableListOf(StepItem())) }
    val stepPhotoPickers = remember { mutableListOf<ManagedActivityResultLauncher<String, Uri?>>() }

    // Scaffold + Body
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar receta", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton { navController.popBackStack() }
                    { Icon(Icons.Default.Close, contentDescription = "Cerrar") }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // --- PHOTO UPLOAD ROW ---
                item {
                    Text("Fotos de receta", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(selectedPhotos) { idx, uri ->
                            Box {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { selectedPhotos.removeAt(idx) },
                                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(MaterialTheme.colors.surface, CircleShape)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colors.error)
                                }
                            }
                        }
                        item {
                            IconButton(
                                onClick = { photoPicker.launch("image/* video/*") },
                                modifier = Modifier.size(80.dp).border(2.dp, GrayExtraLight, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Añadir foto", tint = Orange)
                            }
                        }
                    }
                }

                // --- NAME, DESCRIPTION ---
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre", fontFamily = Poppins) },
                        textStyle = TextStyle(fontFamily = Poppins),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción", fontFamily = Poppins) },
                        textStyle = TextStyle(fontFamily = Poppins),
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }

                // --- DURATION + SERVINGS ---
                item {
                    Text("Duración (min)", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                    Slider(
                        value = duration,
                        onValueChange = { duration = it },
                        valueRange = 10f..120f,
                        steps = durationMarks.size - 2
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        durationMarks.forEach {
                            Text("${it.toInt()}", fontFamily = Poppins, fontSize = 12.sp)
                        }
                    }
                }
                item {
                    Text("Raciones", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (servings>1) servings-- }) { Text("-", fontFamily = Poppins, fontSize = 24.sp) }
                        Text("$servings", fontFamily = Poppins, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { servings++ }) { Text("+", fontFamily = Poppins, fontSize = 24.sp) }
                    }
                }

                // --- INGREDIENTS with drag & drop ---
                item {
                    Text("Ingredientes", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                }
                itemsIndexed(ingredientsList, key = { _, it -> it }) { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .draggedItem(ingredients)  // extensión de reorderable
                            .detectReorderAfterLongPress(ingredients)
                            .padding(vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("☰", fontFamily = Poppins, modifier = Modifier.padding(end = 8.dp))
                            OutlinedTextField(
                                value = item.name,
                                onValueChange = { ingredientsList[index].name = it },
                                placeholder = { Text("Ingrediente", fontFamily = Poppins) },
                                textStyle = TextStyle(fontFamily = Poppins),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.quantity,
                                onValueChange = { ingredientsList[index].quantity = it },
                                placeholder = { Text("Cant.", fontFamily = Poppins) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = TextStyle(fontFamily = Poppins),
                                modifier = Modifier.width(80.dp).padding(start = 8.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            DropdownMenuBox(
                                options = listOf("unid.", "gr.", "ml."),
                                selected = item.unit,
                                onSelect = { ingredientsList[index].unit = it }
                            )
                        }
                    }
                }
                item {
                    OutlinedButton(onClick = { ingredientsList.add(IngredientItem()) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Ingrediente", fontFamily = Poppins)
                    }
                }

                // --- STEPS ---
                item {
                    Text("Paso a paso", fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
                }
                itemsIndexed(stepsList) { idx, step ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${idx+1}.", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = step.description,
                                onValueChange = { stepsList[idx].description = it },
                                placeholder = { Text("Descripción", fontFamily = Poppins) },
                                textStyle = TextStyle(fontFamily = Poppins),
                                modifier = Modifier.weight(1f).height(100.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val stepPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                                it?.let { uri -> stepsList[idx] = step.copy(media = uri) }
                            }
                            if (step.media != null) {
                                AsyncImage(
                                    model = step.media,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { stepsList[idx] = step.copy(media = null) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Eliminar")
                                }
                            } else {
                                IconButton(
                                    onClick = { stepPicker.launch("image/* video/*") },
                                    modifier = Modifier
                                        .size(80.dp)
                                        .border(2.dp, GrayExtraLight, RoundedCornerShape(8.dp))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Añadir media")
                                }
                            }
                        }
                    }
                }
                item {
                    OutlinedButton(onClick = { stepsList.add(StepItem()) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Paso", fontFamily = Poppins)
                    }
                }

                // --- SUBMIT BUTTON ---
                item {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.publishRecipe(
                                PublishRecipeData(
                                    photos = selectedPhotos,
                                    name = name,
                                    description = description,
                                    duration = duration.toInt(),
                                    servings = servings,
                                    ingredients = ingredientsList,
                                    steps = stepsList
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Siguiente", fontFamily = Poppins)
                    }
                }
            }
        }

        // --- POPUPS ---
        if (publishResult == PublishResult.SUCCESS) {
            SuccessDialog(onDismiss = { navController.navigate("home") })
        } else if (publishResult == PublishResult.DUPLICATE) {
            DuplicateDialog(
                onCancel = { /* volver a editar */ },
                onReplace = { /* viewModel.replaceRecipe(...) */ }
            )
        }
    }
}

@Composable
private fun DropdownMenuBox(
    options: List<String>,
    selected: String,
    onSelect: (String)->Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Text(
            text = selected,
            fontFamily = Poppins,
            modifier = Modifier
                .background(GrayExtraLight, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(8.dp)
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(onClick = { onSelect(it); expanded = false }) {
                    Text(it, fontFamily = Poppins)
                }
            }
        }
    }
}

@Composable
private fun SuccessDialog(onDismiss: ()->Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = // tu ilustración aquí
                        painterResource(id = R.drawable.ic_success),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )
                Text("Receta añadida!", fontFamily = Poppins, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Actualmente está pendiente de validación. La encontrarás en “Mis recetas”.",
                    fontFamily = Poppins,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Volver al home", fontFamily = Poppins)
                }
            }
        }
    }
}

@Composable
private fun DuplicateDialog(onCancel: ()->Unit, onReplace: ()->Unit) {
    Dialog(onDismissRequest = onCancel) {
        Surface(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colors.error, modifier = Modifier.size(64.dp))
                Text("Ya tienes una receta con ese nombre", fontFamily = Poppins, fontSize = 18.sp, textAlign = TextAlign.Center)
                Text("¿Deseas reemplazarla?", fontFamily = Poppins, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onCancel) { Text("No, regresar", fontFamily = Poppins) }
                    Button(onClick = onReplace) { Text("Sí, reemplazar", fontFamily = Poppins) }
                }
            }
        }
    }
}
*/