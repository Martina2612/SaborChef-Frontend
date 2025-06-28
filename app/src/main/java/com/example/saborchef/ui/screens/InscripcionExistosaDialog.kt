package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.saborchef.model.Curso
import com.example.saborchef.model.Sede
import com.example.saborchef.ui.theme.Orange
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.model.Cronograma
import com.example.saborchef.ui.theme.BlueDark


@Composable
fun InscripcionExitosaDialog(
    onCerrar: () -> Unit,
    curso: Curso,
    cronograma: Cronograma,
    sede: Sede
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x88000000)), // semitransparente
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter("https://img.freepik.com/vector-premium/diseno-personajes-dibujos-animados-profesionales-ilustracion-vectorial-hermosa_1253044-23589.jpg?semt=ais_items_boosted&w=740"),
                    contentDescription = null,
                    modifier = Modifier
                        .height(120.dp)
                        .padding(bottom = 12.dp)
                )

                Text(
                    text = "¡Inscripción finalizada!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = BlueDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Detalles",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = BlueDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Curso: ${curso.nombre}\n" +
                            "Sede: ${sede.nombreSede}\n" +
                            "Nivel: ${curso.nivel.name}\n" +
                            "Precio: $${curso.precio}\n" +
                            "Inicio de clases: ${cronograma.fechaInicio}\n" +
                            "Modalidad: ${curso.modalidad}",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Te enviamos la factura por mail",
                    fontSize = 15.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onCerrar,
                    colors = ButtonDefaults.buttonColors(containerColor = Orange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Ir a mis cursos", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}





