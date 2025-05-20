package com.example.saborchef.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.saborchef.R
import com.example.saborchef.ui.theme.White
import kotlinx.coroutines.delay

/**
 * Splash Screen con imagen de fondo en lugar de color.
 */
@Composable
fun SplashScreen(
    navController: NavController,
    onTimeout: () -> Unit = { navController.navigate("welcome") }
) {
    val visible = remember { mutableStateOf(false) }
    val isInPreview = LocalInspectionMode.current
    // Si es preview, forzamos visible = true para mostrar logo
    val targetVisible = if (isInPreview) true else visible.value
    val alpha = animateFloatAsState(targetValue = if (targetVisible) 1f else 0f)

    LaunchedEffect(Unit) {
        if (!isInPreview) {
            visible.value = true
            delay(2000)
            onTimeout()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo full screen
        Image(
            painter = painterResource(R.drawable.bg_splash),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Caja blanca con logo animado
        Box(
            modifier = Modifier
                .size(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .alpha(alpha.value)
                .background(White)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo_saborchef),
                contentDescription = "Logo SaborChef",
                modifier = Modifier.size(230.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen() {
    val navController = rememberNavController()
    SplashScreen(navController) {}
}
