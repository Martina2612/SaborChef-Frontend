package com.example.saborchef.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.saborchef.R
import com.example.saborchef.ui.components.DishPosition
import com.example.saborchef.ui.components.DishWithLabel
import com.example.saborchef.ui.components.RoundedWhiteButton
import com.example.saborchef.viewmodel.WelcomeViewModel
import com.example.saborchef.viewmodel.UiState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WelcomeScreen(
    navController: NavController? = null,
    onContinueAsUser: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {}
) {
    // Instancia del ViewModel
    val viewModel: WelcomeViewModel = viewModel()

    // Observa el StateFlow de UiState, que ahora contiene todos los estados posibles.
    val uiState by viewModel.uiState.collectAsState()

    val dishPositions = listOf(
        DishPosition(imageOffsetX = (-80).dp, imageOffsetY = (0).dp, textOffsetX = 40.dp, textOffsetY = 20.dp, startAngle = -80f),
        DishPosition(imageOffsetX = (80).dp, imageOffsetY = 0.dp, textOffsetX = 15.dp, textOffsetY = 20.dp, startAngle = -160f),
        DishPosition(imageOffsetX = (-80).dp,   imageOffsetY = (0).dp, textOffsetX = 40.dp, textOffsetY = 20.dp, startAngle = -80f),
    )

    // El fondo y el gradiente se mantienen constantes para todos los estados.
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_splash),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x80000000), Color(0xCC000000)),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Mantiene los botones abajo
        ) {


            // --- ÁREA DE CONTENIDO DINÁMICO ---
            // Esta parte central cambiará según el UiState.
            // Usamos un Box con weight para que ocupe el espacio disponible.
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Usamos 'when' para decidir qué Composable mostrar
                when (val state = uiState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(color = Color.White)
                    }
                    is UiState.Success -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            state.recipes.take(3).forEachIndexed { index, recipe ->
                                val pos = dishPositions.getOrNull(index) ?: DishPosition()
                                DishWithLabel(
                                    imageUrl = recipe.fotoPrincipal.toString(),
                                    label = recipe.nombre.toString(),
                                    imageOffsetX = pos.imageOffsetX,
                                    imageOffsetY = pos.imageOffsetY,
                                    textOffsetX = pos.textOffsetX,
                                    textOffsetY = pos.textOffsetY,
                                    startAngle = pos.startAngle,
                                    onClick = { navController?.navigate("recipe/${recipe.idReceta}") }
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                    is UiState.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.fetchLatestRecipes() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }

            // Botones siempre en el fondo cuando éxito
            if (uiState is UiState.Success) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RoundedWhiteButton("Continuar como usuario", onContinueAsUser)
                    RoundedWhiteButton("Continuar como invitado", onContinueAsGuest)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    // Para la preview, puedes simular un estado, pero por ahora lo dejamos así.
    WelcomeScreen()
}