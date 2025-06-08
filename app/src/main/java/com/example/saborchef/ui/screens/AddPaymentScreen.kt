package com.example.saborchef.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saborchef.model.Rol
import com.example.saborchef.viewmodel.RegisterViewModel
import com.example.saborchef.viewmodel.RegisterUiState
import com.example.saborchef.viewmodel.SharedAlumnoViewModel
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins

@Composable
fun AddPaymentScreen(
    sharedAlumnoViewModel: SharedAlumnoViewModel,
    registerViewModel: RegisterViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val uiState by registerViewModel.uiState.collectAsState()

    var cardNum by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var tipoTarjeta by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is RegisterUiState.Success -> {
                navController.navigate("verify_registration/${state.auth.email}")
            }
            is RegisterUiState.Error -> {
                errorMessage = state.message
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AttachMoney,
            contentDescription = "Pago",
            tint = OrangeDark,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(16.dp))

        Text(
            "Ingresa un medio de pago\npara tus futuros cursos",
            fontFamily = Poppins,
            fontSize = 20.sp,
            color = BlueDark,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Tarjeta de Crédito", fontWeight = FontWeight.Bold, color = BlueDark)

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = cardNum,
                    onValueChange = { cardNum = it },
                    label = { Text("Número de tarjeta") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Código de seguridad") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = { expiry = it },
                        label = { Text("Vencimiento") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = tipoTarjeta,
                    onValueChange = { tipoTarjeta = it },
                    label = { Text("Tipo Tarjeta") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (cardNum.isBlank() || code.isBlank() || expiry.isBlank() || tipoTarjeta.isBlank()) {
                    errorMessage = "Por favor completá todos los campos"
                    return@Button
                }

                sharedAlumnoViewModel.setCardInfo(cardNum, code, expiry, tipoTarjeta)
                val request = sharedAlumnoViewModel.toRegisterRequest(context)

                if (request.role != Rol.ALUMNO) {
                    errorMessage = "Solo los alumnos deben registrar tarjeta"
                    return@Button
                }

                registerViewModel.register(request)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState !is RegisterUiState.Loading
        ) {
            Text("Confirmar")
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red)
        }
    }
}




