package com.example.saborchef.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.saborchef.model.Clase
import com.example.saborchef.viewmodel.ClasesViewModel
import java.time.LocalDate
import androidx.compose.ui.Alignment
import com.example.saborchef.ui.screens.QrScannerScreen
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext



@Composable
fun AsistenciaTabContent(
    clases: List<Clase>,
    viewModel: ClasesViewModel,
    context: Context,
    mostrarScanner: MutableState<Boolean>
) {
    val asistencias = viewModel.asistencias
    val hoy = LocalDate.now()
    val contextLocal = LocalContext.current
    val tienePermisoCamara = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                contextLocal,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val solicitarPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        tienePermisoCamara.value = granted
    }


    LaunchedEffect(clases) {
        clases.forEach { clase ->
            viewModel.verificarAsistenciaParaClase(context, clase.idClase)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        clases.sortedBy { it.numeroClase }.forEach { clase ->
            val fecha = LocalDate.parse(clase.fechaClase)
            val asistio = asistencias[clase.idClase]
            val puedeEscanearQR = fecha == hoy && asistio != true

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Clase ${clase.numeroClase}: ${clase.titulo}", fontWeight = FontWeight.Bold)
                    Text(clase.fechaClase)
                }

                when {
                    fecha.isAfter(hoy) -> Icon(Icons.Default.Lock, contentDescription = "Futura", tint = Color.Gray)
                    asistio == true -> Box(
                        modifier = Modifier
                            .background(color = Color(0xFFC8E6C9), shape = MaterialTheme.shapes.small)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Asistencia", color = Color(0xFF388E3C), fontWeight = FontWeight.SemiBold)
                    }
                    puedeEscanearQR -> IconButton(onClick = {
                        if (tienePermisoCamara.value) {
                            mostrarScanner.value = true
                        } else {
                            solicitarPermisoCamara.launch(android.Manifest.permission.CAMERA)
                        }
                    }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR", tint = Color.Black)
                    }


                    else -> Box(
                        modifier = Modifier
                            .background(color = Color(0xFFFFCDD2), shape = MaterialTheme.shapes.small)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Asistencia", color = Color(0xFFD32F2F), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Divider()
        }
    }

    if (mostrarScanner.value) {
        QrScannerScreen(
            onCodeScanned = { qrCode ->
                mostrarScanner.value = false
                Log.d("QR_RESULTADO", "Código escaneado: $qrCode")
                // Podés procesar el código, enviar asistencia, etc.
            },
            onClose = { mostrarScanner.value = false }
        )
    }
}

