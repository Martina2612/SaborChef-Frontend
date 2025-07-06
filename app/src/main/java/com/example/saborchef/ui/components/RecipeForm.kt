package com.example.saborchef.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saborchef.models.IngredienteCantidad
import com.example.saborchef.ui.publish.IngredientsSection
import com.example.saborchef.ui.publish.PublishRecipeViewModel
import com.example.saborchef.ui.publish.StepItem
import com.example.saborchef.ui.publish.StepsSection
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeForm(
    initialName: String,
    initialDescription: String,
    initialDuration: Int,
    initialPortions: Int,
    initialType: String,
    initialIngredients: List<IngredienteCantidad>,
    initialSteps: List<StepItem>,
    onSubmit: (String, String, Int, Int, String, List<IngredienteCantidad>, List<StepItem>, List<Uri>) -> Unit,
    viewModel: PublishRecipeViewModel,
    navController: NavController
) {
    val scrollState = rememberScrollState()

    var nombre by remember { mutableStateOf(initialName) }
    var descripcion by remember { mutableStateOf(initialDescription) }
    var duracion by remember { mutableStateOf(initialDuration.toFloat()) }
    var porciones by remember { mutableStateOf(initialPortions) }
    var tipo by remember { mutableStateOf(initialType) }
    var ingredientes = remember { mutableStateListOf(*initialIngredients.toTypedArray()) }
    var mainPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val pickMain = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        mainPhotos = (mainPhotos + uris).take(10)
    }

    var showNombreError by remember { mutableStateOf(false) }
    var showDescripcionError by remember { mutableStateOf(false) }
    var showCategoriaError by remember { mutableStateOf(false) }
    var showFotosError by remember { mutableStateOf(false) }
    var showIngredienteError by remember { mutableStateOf(false) }
    var showPasoError by remember { mutableStateOf(false) }

    Column(
        Modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        PhotosCarousel(
            photos = mainPhotos,
            onRemove = { idx -> mainPhotos = mainPhotos.filterIndexed { i, _ -> i != idx } },
            onAddClick = { pickMain.launch("image/* video/*") }
        )
        if (showFotosError && mainPhotos.isEmpty()) Text("Debe subir al menos una imagen", color = Color.Red, fontSize = 12.sp)

        Spacer(Modifier.height(24.dp))

        CustomTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = "Nombre de la receta",
            isError = showNombreError && nombre.isBlank()
        )

        Spacer(Modifier.height(24.dp))

        CustomTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = "Descripción",
            isError = showDescripcionError && descripcion.isBlank(),
            multiLine = true
        )

        Spacer(Modifier.height(24.dp))

        DurationSlider(duracion) { duracion = it }

        Spacer(Modifier.height(24.dp))

        PortionsSelector(
            portions = porciones,
            onPortionsChange = { porciones = it }
        )

        Spacer(Modifier.height(24.dp))

        CategoryDropdown(tipo) { tipo = it }
        if (showCategoriaError && tipo.isBlank()) Text("Debe seleccionar una categoría", color = Color.Red, fontSize = 12.sp)

        Spacer(Modifier.height(24.dp))

        IngredientsSection(
            ingredientes = ingredientes,
            onRemove = { i -> ingredientes.removeAt(i) },
            onAdd = { ingredientes.add(IngredienteCantidad()) }
        )
        if (showIngredienteError && ingredientes.none { it.nombreIngrediente?.isNotBlank() == true }) Text("Debe agregar al menos un ingrediente", color = Color.Red, fontSize = 12.sp)

        Spacer(Modifier.height(24.dp))

        StepsSection(
            steps = viewModel.steps,
            onAddStep = { viewModel.steps.add(StepItem()) },
            onUpdateDescription = { idx, text -> viewModel.updateStepDescription(idx, text) },
            onAddMedia = { idx, uris -> viewModel.updateStepMedia(idx, (viewModel.steps[idx].media + uris).take(6)) },
            onRemoveMedia = { idx, mediaIdx ->
                val current = viewModel.steps[idx].media.toMutableList()
                current.removeAt(mediaIdx)
                viewModel.updateStepMedia(idx, current)
            }
        )
        if (showPasoError && viewModel.steps.none { it.description.isNotBlank() }) Text("Debe agregar al menos un paso", color = Color.Red, fontSize = 12.sp)

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BlueDark),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent, contentColor = BlueDark)
            ) {
                Text("Volver", fontSize = 16.sp)
            }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = {
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
                        onSubmit(
                            nombre,
                            descripcion,
                            duracion.toInt(),
                            porciones,
                            tipo,
                            ingredientes.toList(),
                            viewModel.steps.toList(),
                            mainPhotos
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeDark, contentColor = Color.White)
            ) {
                Text("Guardar", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    multiLine: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        isError = isError,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = BlueDark,
            unfocusedIndicatorColor = Color(0xFFD0D8E8),
            errorIndicatorColor = Color.Red,
            cursorColor = BlueDark,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = 14.sp),
        maxLines = if (multiLine) 6 else 1,
        singleLine = !multiLine
    )
}


@Composable
fun DurationSlider(
    duration: Float,
    onDurationChange: (Float) -> Unit
) {
    val labels = listOf("10", "30", "45", "60", "80", "100", "120")
    val sliderPositions = listOf(0f, 30f, 45f, 60f, 80f, 100f, 120f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        sliderPositions.forEachIndexed { index, value ->
            Text(
                text = labels[index],
                color = if (duration >= value) OrangeDark else Color.LightGray,
                fontSize = 12.sp
            )
        }
    }

    Slider(
        value = duration,
        onValueChange = onDurationChange,
        valueRange = 0f..120f,
        steps = 5,
        colors = SliderDefaults.colors(
            activeTrackColor = OrangeDark,
            inactiveTrackColor = Color.LightGray,
            thumbColor = OrangeDark
        ),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    )
}

@Composable
fun PortionsSelector(
    portions: Int,
    onPortionsChange: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .border(1.dp, Color(0xFFD0D8E8), RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = { if (portions > 1) onPortionsChange(portions - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = BlueDark)
            }
            Text(
                text = portions.toString(),
                fontSize = 16.sp,
                color = BlueDark,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(
                onClick = { onPortionsChange(portions + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = BlueDark)
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
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
                text = selectedCategory.ifEmpty { "Selecciona un tipo" },
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = BlueDark)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            listOf("Snacks", "Postres", "Carne", "Bebidas", "Pastas", "Tartas", "Ensalada", "Sopas", "Vegetariano", "Vegano")
                .forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category, color = BlueDark) },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
        }
    }
}