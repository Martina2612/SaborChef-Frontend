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
import com.example.saborchef.model.Cronograma
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.screens.InscripcionExitosaDialog
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.viewmodel.CursoViewModel
import com.example.saborchef.viewmodel.SedeViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.window.Dialog
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.ui.theme.OrangeDark

@Composable
fun SucursalDetalleScreen(
    sedeId: Long,
    cronogramaId: Long,
    navController: NavController,
    rol: Rol,
    mostrarBotonConfirmar: Boolean = true
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
    val cronogramaState by cursoViewModel.cronogramaDetalle.collectAsState()
    val curso = cursoState
    val cronograma = cronogramaState

    LaunchedEffect(cronogramaId) {
        cursoViewModel.getCronogramaPorId(cronogramaId)
    }
    LaunchedEffect(Unit) {
        dataStore.token.collect { if (!it.isNullOrBlank()) token = it }
    }
    LaunchedEffect(Unit) {
        dataStore.userId.collect { if (it != null) alumnoId = it }
    }
    LaunchedEffect(cronogramaId) {
        val curso = cursoViewModel.obtenerCursoPorId(cronogramaId)
        cursoViewModel.cargarCurso(curso)
    }
    LaunchedEffect(sedeId) {
        sedeViewModel.obtenerSedePorId(sedeId)
    }

    sedeState?.let { sede ->
        Scaffold(
            bottomBar = {
                BottomBar(navController = navController, role = rol)
            },
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                        .background(OrangeDark)
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sucursal ${sede.nombreSede}",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }



        ) { paddingValues ->
            if (inscripcionExitosa == true && curso != null && cronograma != null) {
                Dialog(onDismissRequest = {
                    cursoViewModel.limpiarEstadoInscripcion()
                }) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 8.dp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        InscripcionExitosaDialog(
                            onCerrar = {
                                cursoViewModel.limpiarEstadoInscripcion()
                                navController.navigate("mis_cursos")
                            },
                            curso = curso,
                            cronograma = cronograma,
                            sede = sede
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Imagen
                Image(
                    painter = rememberAsyncImagePainter(sede.imagenUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Card de datos
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDDDDDD)) // Gris más oscuro
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                    ) {
                        Text("📍 Dirección: ${sede.direccionSede}", color = BlueDark)
                        Spacer(Modifier.height(8.dp))
                        Text("📞 Teléfono: ${sede.telefonoSede}", color = BlueDark)
                        Spacer(Modifier.height(8.dp))
                        Text("📧 Email: ${sede.mailSede}", color = BlueDark)
                        Spacer(Modifier.height(8.dp))
                        Text("📱 WhatsApp: ${sede.whatsapp}", color = BlueDark)
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Volver", color = Color.White)
                    }

                    if (mostrarBotonConfirmar) {
                        Button(
                            onClick = {
                                if (rol == Rol.ALUMNO && alumnoId != null && token != null) {
                                    cursoViewModel.inscribirse(
                                        idCronograma = cronogramaId,
                                        idAlumno = alumnoId!!,
                                        token = token!!
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangeDark)
                        ) {
                            Text("Confirmar", color = Color.White)
                        }
                    }
                }


                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    } ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = OrangeDark)
        }
    }
}



