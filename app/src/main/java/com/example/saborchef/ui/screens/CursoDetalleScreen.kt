package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.ui.theme.*
import com.example.saborchef.viewmodel.CursoViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun CursoDetalleScreen(
    cursoId: Long,
    navController: NavController,
    viewModel: CursoViewModel,
    userRole: Rol = Rol.ALUMNO // Agregar el rol del usuario
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
        Scaffold(
            bottomBar = {
                BottomBar(navController = navController, role = userRole)
            }
        ) { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Box para superponer la flecha sobre la imagen
                Box {
                    // Imagen del curso
                    Image(
                        painter = rememberAsyncImagePainter(curso.imagenUrl),
                        contentDescription = curso.nombre,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Flecha de navegación hacia atrás
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }

                // Card con la información del curso
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Título del curso
                        Text(
                            text = curso.nombre,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlueDark
                        )

                        // Línea decorativa naranja
                        HorizontalDivider(
                            thickness = 3.dp,
                            color = Orange,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primera fila de información
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Duración: ${curso.duracion}",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "120 min c/u",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            // Badge de modalidad
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Orange
                            ) {
                                Text(
                                    text = curso.modalidad,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Información del chef
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar del chef (puedes usar una imagen por defecto o la imagen del curso)
                            Surface(
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp),
                                color = Orange.copy(alpha = 0.2f)
                            ) {
                                // Aquí podrías poner la imagen del chef si la tienes
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = curso.chef.first().toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = Orange
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Chef ${curso.chef}",
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            val cronograma = curso.cronogramas.firstOrNull()
                            val diaSemana = cronograma?.fechaInicio?.let {
                                try {
                                    val fecha = LocalDate.parse(it.toString())
                                    fecha.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es"))
                                        .replaceFirstChar { c -> c.uppercase() }
                                } catch (e: Exception) {
                                    ""
                                }
                            } ?: ""

                            val fechaFormateada = cronograma?.fechaInicio?.let {
                                try {
                                    val fecha = LocalDate.parse(it.toString())
                                    "${fecha.dayOfMonth.toString().padStart(2, '0')}/${fecha.monthValue.toString().padStart(2, '0')}"
                                } catch (e: Exception) {
                                    ""
                                }
                            } ?: ""

                            Column(horizontalAlignment = Alignment.End) {
                                if (diaSemana.isNotBlank()) {
                                    Text(text = diaSemana, color = Color.Gray, fontSize = 12.sp)
                                }
                                if (fechaFormateada.isNotBlank()) {
                                    Text(text = "Inicio: $fechaFormateada", color = Color.Gray, fontSize = 12.sp)
                                }
                            }

                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nivel y precio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nivel: ${curso.nivel.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$ ${curso.precio}",
                                fontWeight = FontWeight.Bold,
                                color = BlueDark,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Tabs de Descripción y Cronograma
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TabButton("Descripción", selectedTab == 0) { selectedTab = 0 }
                            TabButton("Cronograma", selectedTab == 1) { selectedTab = 1 }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Contenido de las tabs
                        if (selectedTab == 0) {
                            Text(
                                text = curso.descripcion,
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                curso.contenidos.split(",").forEach { tema ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Orange,
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.padding(4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = tema.trim(),
                                            color = BlueDark,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón de inscripción
                        Button(
                            onClick = {
                                // TODO: Implementar lógica de inscripción
                                println("Inscripción al curso: ${curso.nombre}")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Orange),
                            shape = RoundedCornerShape(25.dp)
                        ) {
                            Text(
                                text = "Quiero inscribirme!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    } ?: run {
        // Mostrar loading mientras se carga el curso con Scaffold
        Scaffold(
            bottomBar = {
                BottomBar(navController = navController, role = userRole)
            }
        ) { paddingValues ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Orange)
            }
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
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontWeight = fontWeight, color = textColor, fontSize = 14.sp)
    }
}

