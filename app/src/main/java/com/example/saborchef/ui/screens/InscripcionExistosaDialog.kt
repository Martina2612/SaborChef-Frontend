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

@Composable
fun InscripcionExitosaDialog(
    onCerrar: () -> Unit,
    curso: Curso,
    sede: Sede
) {
    AlertDialog(
        onDismissRequest = { onCerrar() },
        confirmButton = {
            TextButton(onClick = { onCerrar() }) {
                Text("Aceptar", color = Orange)
            }
        },
        title = { Text("¡Inscripción Exitosa!") },
        text = {
            Column {
                Text("Te has inscrito al curso:")
                Text(curso.nombre, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("En la sede:")
                Text(sede.nombreSede, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}
