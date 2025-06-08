package com.example.saborchef.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark

@Composable
fun SimpleHomeScreen(nombre: String?) {
    val saludo = if (nombre.isNullOrBlank()) "Hola invitado" else "Hola $nombre"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = saludo,
            fontSize = 28.sp,
            color = BlueDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Bienvenido a SaborChef!",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
