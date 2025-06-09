package com.example.saborchef.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.CardDefaults.cardColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentScreen(
    sharedAlumnoViewModel: SharedAlumnoViewModel,
    navController: NavController,
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var cardNum by remember { mutableStateOf("") }
    var cardNumError by remember { mutableStateOf<String?>(null) }

    var code by remember { mutableStateOf("") }
    var codeError by remember { mutableStateOf<String?>(null) }

    var expiry by remember { mutableStateOf("") }
    var expiryError by remember { mutableStateOf<String?>(null) }

    var tipoTarjeta by remember { mutableStateOf("") }
    var tipoTarjetaError by remember { mutableStateOf<String?>(null) }

    var formError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is RegisterUiState.Success -> {
                navController.navigate("verify_registration/${(uiState as RegisterUiState.Success).auth.email}")
            }
            is RegisterUiState.Error -> {
                formError = (uiState as RegisterUiState.Error).message
            }
            else -> {}
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AttachMoney,
            contentDescription = null,
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
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Tarjeta de Crédito",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark
                )

                Spacer(Modifier.height(12.dp))
                // Número de tarjeta
                OutlinedTextField(
                    value = cardNum,
                    onValueChange = {
                        if (it.length <= 16 && it.all(Char::isDigit)) {
                            cardNum = it
                            cardNumError = null
                        }
                    },
                    label = { Text("Número de tarjeta", fontFamily = Poppins, color = BlueDark) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = cardNumError != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                cardNumError?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                }

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Código de seguridad
                    OutlinedTextField(
                        value = code,
                        onValueChange = {
                            if (it.length <= 3 && it.all(Char::isDigit)) {
                                code = it
                                codeError = null
                            }
                        },
                        label = { Text("Código (3 dígitos)", fontFamily = Poppins, color = BlueDark) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = codeError != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = expiry,
                        onValueChange = {
                            // Permitir MM/AA: máximo 5 chars, formato ##/##
                            if (it.length <= 5 && it.replace("/", "").all(Char::isDigit)) {
                                expiry = when {
                                    it.length == 2 && !it.contains("/") -> "$it/"
                                    else -> it
                                }
                                expiryError = null
                            }
                        },
                        label = { Text("Vencimiento (MM/AA)", fontFamily = Poppins, color = BlueDark) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = expiryError != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                codeError?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                }
                expiryError?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                }

                Spacer(Modifier.height(12.dp))
                // Tipo de tarjeta
                OutlinedTextField(
                    value = tipoTarjeta,
                    onValueChange = {
                        tipoTarjeta = it
                        tipoTarjetaError = null
                    },
                    label = { Text("Tipo de tarjeta (Visa/MasterCard…)", fontFamily = Poppins, color = BlueDark) },
                    singleLine = true,
                    isError = tipoTarjetaError != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                tipoTarjetaError?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                // Validaciones
                var valid = true
                if (cardNum.length != 16) {
                    cardNumError = "Debe tener 16 dígitos"
                    valid = false
                }
                if (code.length != 3) {
                    codeError = "Debe tener 3 dígitos"
                    valid = false
                }
                if (!expiry.matches(Regex("""\d{2}/\d{2}"""))) {
                    expiryError = "Formato inválido"
                    valid = false
                }
                if (tipoTarjeta.isBlank()) {
                    tipoTarjetaError = "Completa este campo"
                    valid = false
                }
                if (!valid) return@Button

                // Lógica original
                sharedAlumnoViewModel.setCardInfo(cardNum, code, expiry, tipoTarjeta)
                if (sharedAlumnoViewModel.rol != Rol.ALUMNO) {
                    formError = "Solo los alumnos deben registrar tarjeta"
                    return@Button
                }
                viewModel.register(context, sharedAlumnoViewModel)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = uiState !is RegisterUiState.Loading
        ) {
            Text("Confirmar", fontFamily = Poppins)
        }

        formError?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Color.Red, textAlign = TextAlign.Center)
        }
    }
}



