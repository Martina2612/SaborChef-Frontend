package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.saborchef.model.Curso
import com.example.saborchef.ui.theme.*
import com.example.saborchef.viewmodel.CursoViewModel
import kotlinx.coroutines.launch

@Composable
fun CursoDetalleScreen(
    cursoId: Long,
    navController: NavController,
    viewModel: CursoViewModel
) {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Descripción, 1 = Cronograma

    // Cargar el curso cuando se inicia la pantalla
    LaunchedEffect(cursoId) {
        scope.launch {
            viewModel.getCursoPorId(cursoId)
        }
    }

    // Observar el estado del curso
    val cursoDetalle by viewModel.cursoDetalle.collectAsState()

    // Mostrar el contenido o loading
    cursoDetalle?.let { curso ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = rememberAsyncImagePainter(curso.imagenUrl),
                contentDescription = curso.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(curso.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BlueDark)
                Spacer(Modifier.height(8.dp))

                HorizontalDivider(thickness = 2.dp, color = Orange)
                Spacer(Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Duración: ${curso.duracion}", color = Color.Gray)
                    Text(curso.modalidad, color = OrangeDark, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Nivel: ${curso.nivel.name.lowercase().replaceFirstChar { it.uppercase() }}", color = Color.Gray)
                    Text("$${curso.precio}", color = Color.Gray)
                }

                Spacer(Modifier.height(4.dp))
                Text("Chef ${curso.chef}", color = Color.Gray)

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth()) {
                    TabButton("Descripción", selectedTab == 0) { selectedTab = 0 }
                    TabButton("Cronograma", selectedTab == 1) { selectedTab = 1 }
                }

                Spacer(Modifier.height(8.dp))

                if (selectedTab == 0) {
                    Text(curso.descripcion, color = Color.DarkGray, fontSize = 14.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        curso.contenidos.split(",").forEach { tema ->
                            Text("\u2022 ${tema.trim()}", color = BlueDark)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        // TODO: Implementar lógica de inscripción
                        // Ejemplo: navController.navigate("inscripcion/$cursoId")
                        println("Inscripción al curso: ${curso.nombre}")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Text("Quiero inscribirme!", color = Color.White)
                }
            }
        }
    } ?: run {
        // Mostrar loading mientras se carga el curso
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun RowScope.TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) Color.White else Color.LightGray.copy(alpha = 0.3f)
    val fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
    val textColor = if (selected) Orange else BlueDark

    Box(
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontWeight = fontWeight, color = textColor)
    }
}

