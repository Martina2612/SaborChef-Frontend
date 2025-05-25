package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.R
import com.example.saborchef.ui.components.DishPosition
import com.example.saborchef.ui.components.DishWithLabel
import com.example.saborchef.ui.components.RoundedWhiteButton
import com.example.saborchef.ui.viewmodel.WelcomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saborchef.infrastructure.ApiClient

@Composable
fun WelcomeScreen(
    navController: NavController? = null,
    onContinueAsUser: () -> Unit = {},
    onContinueAsGuest: () -> Unit = {}
) {
    // Instancia del ViewModel
    val viewModel: WelcomeViewModel = viewModel()

    // Observa el StateFlow de recetas con delegación
    val recetas by viewModel.recetas.collectAsState()
    val featuredRecipes = recetas.take(3)

    val cliente= ApiClient(baseUrl = "http://192.168.1.37:8080/")

    val dishPositions = listOf(
        DishPosition(imageOffsetX = (-80).dp, imageOffsetY = (-20).dp, textOffsetX = 70.dp, textOffsetY = 30.dp, startAngle = -60f),
        DishPosition(imageOffsetX = (-80).dp, imageOffsetY = 200.dp, textOffsetX = 35.dp, textOffsetY = 30.dp, startAngle = -130f),
        DishPosition(imageOffsetX = 60.dp,   imageOffsetY = (-180).dp, textOffsetX = 70.dp, textOffsetY = 30.dp, startAngle = -60f),
    )

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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                featuredRecipes.forEachIndexed { index, recipe ->
                    val pos = dishPositions.getOrNull(index) ?: DishPosition(0.dp, 0.dp)
                    DishWithLabel(
                        imageUrl = recipe.fotoPrincipal as String,
                        label = recipe.nombre as String,
                        imageOffsetX = pos.imageOffsetX,
                        imageOffsetY = pos.imageOffsetY,
                        textOffsetX = pos.textOffsetX,
                        textOffsetY = pos.textOffsetY,
                        startAngle = pos.startAngle,
                        onClick = { navController?.navigate("recipe/${recipe.idReceta}") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Column(
                modifier = Modifier
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

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()
}