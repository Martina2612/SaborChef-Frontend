package com.example.saborchef.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.screens.InscripcionExitosaDialog
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.viewmodel.CursoViewModel
import com.example.saborchef.viewmodel.SedeViewModel
import kotlinx.coroutines.launch

@Composable
fun SucursalDetalleScreen(
    sedeId: Long,
    cronogramaId: Long,
    navController: NavController,
    rol: Rol
) {
    val sedeViewModel: SedeViewModel = viewModel()
    val cursoViewModel: CursoViewModel = viewModel()
    val sedeState by sedeViewModel.sede.collectAsState()
    val cursoState by cursoViewModel.cursoDetalle.collectAsState()
    val inscripcionExitosa by cursoViewModel.inscripcionExitosa.collectAsState()

    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    var token by remember { mutableStateOf<String?>(null) }
    var alumnoId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        dataStore.token.collect { newToken ->
            if (!newToken.isNullOrBlank()) {
                token = newToken
            }
        }
    }
    LaunchedEffect(Unit) {
        dataStore.userId.collect { id ->
            if (id != null) {
                alumnoId = id
            }
        }
    }





    // Obtener curso por cronogramaId
    LaunchedEffect(cronogramaId) {
        val curso = cursoViewModel.obtenerCursoPorId(cronogramaId)
        cursoViewModel.cargarCurso(curso)
    }

    // Obtener sede
    LaunchedEffect(sedeId) {
        sedeViewModel.obtenerSedePorId(sedeId)
    }

    sedeState?.let { sede ->
        Scaffold(
            bottomBar = { /* opcional */ }
        ) { paddingValues ->

            // Diálogo de inscripción exitosa
            if (inscripcionExitosa == true) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000)),
                    contentAlignment = Alignment.Center
                ) {
                    cursoState?.let { curso ->
                        InscripcionExitosaDialog(
                            onCerrar = {
                                cursoViewModel.limpiarEstadoInscripcion()
                                navController.navigate("mis_cursos")
                            },
                            curso = curso,
                            sede = sede
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Sucursal ${sede.nombreSede}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Orange,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Image(
                    painter = rememberAsyncImagePainter(sede.imagenUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("📍 Dirección: ${sede.direccionSede}", color = BlueDark)
                Text("📞 Teléfono: ${sede.telefonoSede}", color = BlueDark)
                Text("📧 Email: ${sede.mailSede}", color = BlueDark)
                Text("📱 WhatsApp: ${sede.whatsapp}", color = BlueDark)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Volver")
                    }


                    Button(
                        onClick = {
                            println("Intentando inscribir con:")
                            println("Token: $token")
                            println("Alumno ID: $alumnoId")
                            println("Cronograma ID: $cronogramaId")


                            if (rol == Rol.ALUMNO && alumnoId != null && token != null) {
                                cursoViewModel.inscribirse(
                                    idCronograma = cronogramaId,
                                    idAlumno = alumnoId!!,
                                    token = token!!
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) {
                        Text("Confirmar", color = Color.White)
                    }
                }
            }
        }
    } ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Orange)
        }
    }
}


