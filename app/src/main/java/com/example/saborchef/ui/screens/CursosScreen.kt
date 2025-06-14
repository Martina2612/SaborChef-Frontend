package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.model.Nivel
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.viewmodel.CursoUiState
import com.example.saborchef.viewmodel.CursoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CursosScreen(
    navController: NavController,
    viewModel: CursoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is CursoUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CursoUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is CursoUiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.cursos) { curso ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(curso.imagenUrl),
                                contentDescription = curso.nombre,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = curso.nombre,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlueDark
                                )
                                Text(
                                    text = "Chef: ${curso.chef}",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Nivel: ${nivelToText(curso.nivel)}",
                                    fontSize = 14.sp,
                                    color = BlueLight
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangeDark),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                                ) {
                                    Text(curso.modalidad)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            TextButton(onClick = {
                                // navController.navigate("curso_detalle/${curso.id}")
                            }) {
                                Text("Ver", color = BlueLight)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun nivelToText(nivel: Nivel): String {
    return when (nivel) {
        Nivel.PRINCIPIANTE -> "Principiante"
        Nivel.INTERMEDIO -> "Intermedio"
        Nivel.AVANZADO -> "Avanzado"
    }
}



