package com.example.saborchef.ui.screens

import com.example.saborchef.ui.components.DetallesTabContent



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import com.example.saborchef.model.CursoInscripto
import com.example.saborchef.ui.theme.Orange

@Composable
fun MisCursosDetalleScreen(curso: CursoInscripto, navController: NavController) {
    val tabs = listOf("Detalles", "Cronograma", "Asistencia")
    var selectedTabIndex by remember { mutableIntStateOf(0) } // reemplazo sugerido

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Orange)
                    .padding(top = 12.dp, bottom = 12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = " ${curso.nombreCurso}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { contentPadding -> // ✅ corregido nombre
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(curso.imagenUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        selectedContentColor = Orange,
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> DetallesTabContent(curso)
                1 -> { /* Cronograma */ }
                2 -> { /* Asistencia */ }
            }
        }
    }
}


