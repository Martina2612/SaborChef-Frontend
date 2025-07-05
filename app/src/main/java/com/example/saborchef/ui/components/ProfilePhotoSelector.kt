package com.example.saborchef.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.saborchef.ui.theme.BlueDark

@Composable
fun ProfilePhotoSelector(
    currentPhotoUrl: String?,
    currentPhotoUri: Uri?,
    onPhotoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    // Launcher para seleccionar foto de galería
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onPhotoSelected(it) }
    }

    Surface(
        modifier = modifier
            .size(95.dp)
            .clip(CircleShape)
            .clickable { photoLauncher.launch("image/*") },
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                // Mostrar foto URI seleccionada (nueva)
                currentPhotoUri != null -> {
                    AsyncImage(
                        model = currentPhotoUri,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Mostrar foto URL del servidor (existente)
                !currentPhotoUrl.isNullOrBlank() -> {
                    AsyncImage(
                        model = currentPhotoUrl,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Mostrar placeholder
                else -> {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Placeholder",
                        tint = BlueDark.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }

            // Overlay con ícono de cámara
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cambiar foto",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}