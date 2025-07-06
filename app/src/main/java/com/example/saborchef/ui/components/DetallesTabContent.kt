package com.example.saborchef.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.layout.ContentScale // Removing unused import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.model.CursoInscripto
import com.example.saborchef.model.BajaCursoResponse
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.viewmodel.CursoViewModel
import com.example.saborchef.viewmodel.BajaUiState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.saborchef.data.DataStoreManager
import android.widget.Toast

@Composable
fun DetallesTabContent(
    curso: CursoInscripto,
    navController: NavController,
    viewModel: CursoViewModel = viewModel()
) {
    val context = LocalContext.current
    val dataStore = DataStoreManager(context)

    // Estados del ViewModel
    val bajaUiState by viewModel.bajaUiState.collectAsState()

    // Estados del DataStore
    val token by dataStore.token.collectAsState(initial = "")
    val userId by dataStore.userId.collectAsState(initial = null)

    var showBajaDialog by remember { mutableStateOf(false) }

    // Manejar estados de baja - Usar variable local para evitar smart cast
    LaunchedEffect(bajaUiState) {
        val currentState = bajaUiState
        when (currentState) {
            is BajaUiState.ReintegroCalculado -> {
                showBajaDialog = true
            }
            is BajaUiState.BajaExitosa -> {
                showBajaDialog = false
                Toast.makeText(context, currentState.mensaje, Toast.LENGTH_LONG).show()
                navController.navigate("mis_cursos") {
                    popUpTo("mis_cursos") { inclusive = true }
                }
            }
            is BajaUiState.Error -> {
                showBajaDialog = false
                Toast.makeText(context, currentState.mensaje, Toast.LENGTH_LONG).show()
            }
            else -> { /* No hacer nada */ }
        }
    }

    // Dialog con información de reintegro - Usar when expression
    val currentBajaState = bajaUiState
    when (currentBajaState) {
        is BajaUiState.ReintegroCalculado -> {
            BajaCursoDialog(
                isVisible = showBajaDialog,
                onDismiss = {
                    showBajaDialog = false
                    viewModel.limpiarEstadoBaja()
                },
                onConfirm = {
                    // Ejecutar la baja - Fix token nullability
                    userId?.let { id ->
                        val userToken = token.takeIf { !it.isNullOrBlank() } ?: ""
                        viewModel.ejecutarBaja(curso.idCronograma, id, userToken)
                    }
                },
                reintegroInfo = currentBajaState.reintegroInfo,
                isLoading = currentBajaState is BajaUiState.EjecutandoBaja // Fix condition
            )
        }
        else -> { /* No mostrar dialog */ }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Progreso
        LinearProgressIndicator(
            progress = curso.progreso / 100f,
            color = Orange,
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(RoundedCornerShape(6.dp))
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Datos del curso
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Duración: ${curso.duracion ?: "N/A"}", color = Color.DarkGray)
            Text("60 min c/u", color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://ruta-avatar-chef.png"),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("  ${curso.chef ?: "Desconocido"}", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(curso.modalidad ?: "Sin modalidad", fontSize = 14.sp) // Fix nullability
        Spacer(modifier = Modifier.height(6.dp))
        Text("$ ${curso.precio ?: 0.0}", fontSize = 14.sp) // Fix nullability para precio

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { navController.navigate("sucursal_detalle/${curso.sede.idSede}/${curso.idCronograma}/false") },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Ver sede", color = Color.White)
            }

            Button(
                onClick = { },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x3329a745)
                ),
                modifier = Modifier.height(40.dp)
            ) {
                Text("Inscripto", color = Color(0xFF2E7D32))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Descripción y Requisitos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF374957))
        Spacer(modifier = Modifier.height(6.dp))
        Text(curso.descripcion ?: "Sin descripción", fontSize = 14.sp, color = Color(0xFF6C7A89)) // Fix nullability

        Spacer(modifier = Modifier.height(16.dp))

        // Botón con manejo correcto de estados
        Button(
            onClick = {
                // PASO 1: Calcular reintegro antes de mostrar dialog
                userId?.let { id ->
                    viewModel.calcularReintegro(curso.idCronograma, id)
                }
            },
            enabled = bajaUiState !is BajaUiState.CalculandoReintegro,
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Usar variable local para evitar smart cast
            val buttonState = bajaUiState
            when (buttonState) {
                is BajaUiState.CalculandoReintegro -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                else -> {
                    Text("Quiero darme de baja", color = Color.White)
                }
            }
        }
    }
}

// Componente del dialog (mismo que antes)
@Composable
fun BajaCursoDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    reintegroInfo: BajaCursoResponse,
    isLoading: Boolean = false
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "¿Está seguro que desea darse de baja del curso?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = reintegroInfo.mensaje,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "Sí, confirmar",
                            color = Color.White
                        )
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onDismiss,
                    enabled = !isLoading
                ) {
                    Text("No, regresar")
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}