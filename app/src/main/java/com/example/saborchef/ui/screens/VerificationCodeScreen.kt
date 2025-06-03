package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.RegisterUiState
import com.example.saborchef.viewmodel.RegisterViewModel
import kotlinx.coroutines.delay

@Composable
fun VerificationCodeScreen(
    email: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onResendCode: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel()
) {
    var code by remember { mutableStateOf(List(4) { "" }) }
    var timeLeft by remember { mutableLongStateOf(86400L) } // 24hs en segundos
    val uiState by viewModel.uiState.collectAsState()

    // Disminuir tiempo
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        onResendCode() // opcional: reintentar o notificar
    }

    // Si se confirmó exitosamente
    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.SuccessUnit) {
            onNext()
        }
    }

    val timeFormatted = String.format("%02d:%02d:%02d", timeLeft / 3600, (timeLeft % 3600) / 60, timeLeft % 60)
    val codeStr = code.joinToString("")

    Box(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).size(36.dp).background(Color.White, CircleShape)) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = BlueDark)
            }
            Text("Revisa tu mail", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = BlueDark)
            Spacer(Modifier.height(8.dp))
            Text("Por favor, ingresa el código que enviamos a tu mail (duración máx. 24hs)", textAlign = TextAlign.Center, fontFamily = Poppins, fontSize = 14.sp, color = BlueDark)
            Spacer(Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                code.forEachIndexed { index, char ->
                    OutlinedTextField(
                        value = char,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                code = code.toMutableList().also { it[index] = newValue }
                            }
                        },
                        modifier = Modifier.width(60.dp).height(64.dp),
                        textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 20.sp),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
            Text("El código expira en $timeFormatted", color = if (timeLeft < 60) Color.Red else BlueDark, fontFamily = Poppins)
            Spacer(Modifier.height(32.dp))

            AppButton(text = "Siguiente", onClick = {
                if (codeStr.length == 4) {
                    viewModel.confirmarCuenta(email, codeStr)
                }
            })

            Spacer(Modifier.height(16.dp))
            AppButton(text = "Enviarlo nuevamente", onClick = onResendCode, primary = false)
        }
    }
}
