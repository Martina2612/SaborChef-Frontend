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
import androidx.compose.ui.unit.sp
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
    var claseParaQR by remember { mutableStateOf<Clase?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var mensajeAsistencia by remember { mutableStateOf<String?>(null) }

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
        // Mostrar mensaje de resultado
        mensajeAsistencia?.let { mensaje ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                backgroundColor = if (mensaje.contains("exitosa", true)) Color(0xFFC8E6C9) else Color(0xFFFFCDD2)
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(16.dp),
                    color = if (mensaje.contains("exitosa", true)) Color(0xFF388E3C) else Color(0xFFD32F2F),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

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
                    puedeEscanearQR -> {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF388E3C)
                            )
                        } else {
                            IconButton(onClick = {
                                if (tienePermisoCamara.value) {
                                    claseParaQR = clase
                                    mostrarScanner.value = true
                                } else {
                                    solicitarPermisoCamara.launch(android.Manifest.permission.CAMERA)
                                }
                            }) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR", tint = Color.Black)
                            }
                        }
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

    // Scanner con lógica de asistencia
    if (mostrarScanner.value && claseParaQR != null) {
        QrScannerScreen(
            onCodeScanned = { qrCode ->
                mostrarScanner.value = false
                isLoading = true
                mensajeAsistencia = null

                Log.d("QR_ESCANEADO", "Código escaneado: '$qrCode' para clase: ${claseParaQR!!.idClase}")

                // Registrar asistencia - Cualquier QR sirve
                viewModel.registrarAsistenciaConQR(
                    context = context,
                    claseId = claseParaQR!!.idClase,
                    qrCode = qrCode,
                    onResult = { success, mensaje ->
                        isLoading = false
                        mensajeAsistencia = mensaje
                        if (success) {
                            // Actualizar estado de asistencia
                            viewModel.verificarAsistenciaParaClase(context, claseParaQR!!.idClase)
                        }
                        // Limpiar mensaje después de 5 segundos
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            mensajeAsistencia = null
                        }, 5000)
                    }
                )
            },
            onClose = {
                mostrarScanner.value = false
                claseParaQR = null
            }
        )
    }
}

