package com.example.saborchef.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.ui.components.AppButton
import kotlinx.coroutines.delay
import com.example.saborchef.R
import com.example.saborchef.viewmodel.StudentUiState

/**
 * Pantalla de éxito de registro/escalado para alumnos.
 * Dispara la conversión al backend al pulsar "Finalizar".
 */
@Composable
fun RegistrationSuccessScreen(
    isLoading: Boolean,
    errorMessage: String?,
    isSuccess: Boolean,
    onFinalize: () -> Unit,
    onSuccess: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de éxito (reemplaza R.drawable.icon_check según convenga)
            // Puedes reutilizar SuccessScreen's Image si lo deseas
            Image(
                painter = painterResource(id = R.drawable.icon_check),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¡Registro completado!",
                fontFamily = Poppins,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BlueDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            AppButton(
                text = if (isLoading) "Procesando..." else "Finalizar",
                onClick = {
                    Log.d("DEBUG", "Finalizar pulsado")
                    onFinalize()
                }
            )
        }
    }

    // Cuando termine con éxito, disparo la navegación
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            // breve retardo si deseas mostrar indicación de éxito
            delay(200)
            onSuccess()
        }
    }
}
