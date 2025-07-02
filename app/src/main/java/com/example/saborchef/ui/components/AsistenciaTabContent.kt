package com.example.saborchef.ui.components

import android.content.Context
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
            val color = when {
                fecha.isAfter(hoy) -> Color.Gray
                asistio == true -> Color(0xFF4CAF50) // verde
                else -> Color(0xFFF44336) // rojo
            }
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
                    Text(clase.fechaClase.toString())
                }

                when {
                    fecha.isAfter(hoy) -> Icon(Icons.Default.Lock, contentDescription = "Futura", tint = Color.Gray)
                    asistio == true -> Text("Asistencia", color = color)
                    puedeEscanearQR -> IconButton(onClick = {
                        // Abrir escáner QR acá
                    }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear QR", tint = Color.Black)
                    }
                    else -> Text("Asistencia", color = color)
                }
            }

            Divider()
        }
    }
}
