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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.model.CursoInscripto
import com.example.saborchef.ui.theme.Orange


@Composable
fun DetallesTabContent(curso: CursoInscripto) {
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

        Text(curso.modalidad, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text("$ ${curso.precio}", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { /* Navegar */ },
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
        Text(curso.descripcion, fontSize = 14.sp, color = Color(0xFF6C7A89))


        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* darte de baja */ },
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Quiero darme de baja", color = Color.White)
        }
    }
}
