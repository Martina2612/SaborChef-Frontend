package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.Button
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
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
            RecipeDetailContent(recipe = recipe, onBack = onBack)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeDetailContent(
    recipe: RecetaDetalleResponse,
    onBack: () -> Unit = {}
) {
    // Null-safe lists
    val fotos = recipe.fotos.orEmpty()
    val ingredientes = recipe.ingredientes.orEmpty()
    val pasos = recipe.pasos.orEmpty()
    val comentarios = recipe.comentarios.orEmpty()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(ScrollState(0))
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
                Image(
                    painter = rememberAsyncImagePainter(fotos[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
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
        }

        Text(
            text = recipe.nombre.orEmpty(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = BlueDark,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Fila: usuario (izq) y estrellas (der)
        val rating = recipe.promedioCalificacion?.toInt() ?: 0
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recipe.nombreUsuario.orEmpty(),
                fontSize = 16.sp,
                color = BlueDark
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { idx ->
                    val icon = if (idx < rating) Icons.Filled.Star else Icons.Outlined.StarBorder
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BlueDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Fila: duración (izq) y porciones (der)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "⏱ ${recipe.duracion} minutos",
                fontSize = 16.sp,
                color = BlueDark
            )
            Text(
                text = "👥 ${recipe.porciones} porciones",
                fontSize = 16.sp,
                color = BlueDark
            )
        }

        Spacer(Modifier.height(16.dp))


    //Ingredientes
        Text(
            "Ingredientes",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = BlueDark,
            modifier = Modifier.padding(start = 16.dp)
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
                    val stepPager = rememberPagerState()
                    HorizontalPager(
                        count = paso.contenidos.orEmpty().size,
                        state = stepPager,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(top = 8.dp)
                    ) { page ->
                        val url = paso.contenidos.orEmpty()[page].url.orEmpty()
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
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
                        repeat(paso.contenidos.orEmpty().size) { idx2 ->
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

        Text(
            "Comentarios",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = BlueDark,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        comentarios.forEach { c ->
            Column(Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)) {
                Text(c.nombreUsuario.orEmpty(), fontWeight = FontWeight.Medium, color = BlueDark)
                Text(c.texto.orEmpty(), fontSize = 16.sp, color = Color.DarkGray)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecipeDetailScreenPreview() {
    // Preview with dummy data if needed
    RecipeDetailScreen(recipeId = "0", onBack = {})
}
