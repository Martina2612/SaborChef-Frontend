package com.example.saborchef.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun PasswordCodeScreen(
    email: String,
    code: String,
    onCodeChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Icono de volver
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color(0xFF3E5481))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título
        Text(
            text = "Revisá tu mail",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF3E5481)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Por favor, ingresá el código que enviamos a tu mail",
            style = MaterialTheme.typography.body2,
            color = Color(0xFF7D97CB)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo para el código
        TextField(
            value = code,
            onValueChange = onCodeChange,
            label = { Text("Código") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = MaterialTheme.colors.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF8F52))
        ) {
            Text(text = if (isLoading) "Verificando..." else "Siguiente", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                Toast.makeText(context, "Reenviar no implementado", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Enviarlo nuevamente", color = Color(0xFF3E5481))
        }
    }
}




