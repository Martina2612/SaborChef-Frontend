package com.example.saborchef.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins

@Composable
fun MostrarPantallaSerAlumno(
    onQuieroSerAlumno: () -> Unit,
    onContinuar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "Ups! Debes ser Alumno para poder inscribirte a un curso.",
            color = BlueDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Texto explicativo
        Text(
            text = "Como alumno podrás acceder a todos los detalles de los cursos e inscribirte en ellos.",
            color = Color.Gray,
            fontSize = 14.sp,
            fontFamily = Poppins,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botón principal - Quiero ser Alumno
        Button(
            onClick = onQuieroSerAlumno,
            colors = ButtonDefaults.buttonColors(containerColor = OrangeDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Quiero ser Alumno",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón secundario - Continuar viendo cursos
        OutlinedButton(
            onClick = onContinuar,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeDark),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Continuar viendo cursos",
                color = OrangeDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins
            )
        }
    }
}