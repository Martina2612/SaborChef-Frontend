package com.example.saborchef.ui.components

import android.content.Context
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


@Composable
fun AsistenciaTabContent(
    clases: List<Clase>,
    viewModel: ClasesViewModel,
    context: Context
) {
    val asistencias = viewModel.asistencias
    val hoy = LocalDate.now()

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
                        // Abrir escáner QR acá
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
}

