package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saborchef.R
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.models.PaymentMethod
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    // Manejo seguro de valores con valores por defecto
    val alias by dataStore.alias.collectAsState(initial = null)
    val numeroTarjeta by dataStore.numeroTarjeta.collectAsState(initial = "")
    val medioPago by dataStore.tipoTarjeta.collectAsState(initial = "VISA")
    val fechaVencimiento by dataStore.fechaVencimiento.collectAsState(initial = "")

    // Depuración de valores
    LaunchedEffect(alias, numeroTarjeta, medioPago) {
        println("Datos de pago actuales:")
        println("Alias: $alias")
        println("Número tarjeta: $numeroTarjeta")
        println("Tipo tarjeta: $medioPago")
        println("Fecha vencimiento: $fechaVencimiento")
    }

    // PaymentMethod con manejo robusto de nulabilidad
    val paymentMethod = remember(alias, medioPago, numeroTarjeta) {
        PaymentMethod(
            id = 1L,
            alias = alias ?: "Usuario", // Valor por defecto
            tipo = medioPago.ifBlank { "VISA" }.uppercase(),
            numeroTarjeta = numeroTarjeta.ifBlank { "**** **** **** 1234" } // Valor por defecto
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis medios de pago",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = BlueDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = BlueDark
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Tarjeta grande con la información del medio de pago
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (paymentMethod.tipo) {
                        "VISA" -> Color(0xFF1A1F71)
                        "MASTERCARD" -> Color(0xFFEB001B)
                        else -> OrangeDark
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Icono y tipo de tarjeta
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )

                        Text(
                            text = paymentMethod.tipo,
                            color = Color.White,
                            fontFamily = Poppins,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    // Alias del usuario (con valor por defecto si es null)
                    Text(
                        text = paymentMethod.alias,
                        color = Color.White,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Número de tarjeta (usando el valor real o el por defecto)
                    Text(
                        text = paymentMethod.numeroTarjeta ?: "**** **** **** ****", // Solución 1
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información del medio de pago",
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BlueDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    InfoRow("Titular:", paymentMethod.alias)
                    InfoRow("Tipo:", paymentMethod.tipo)
                    InfoRow("Número:", paymentMethod.numeroTarjeta)
                    InfoRow("Vencimiento:", fechaVencimiento.ifBlank { "MM/AA" })
                    InfoRow("Estado:", "Activo")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón para añadir/editar medio de pago
            Button(
                onClick = { navController.navigate("add_payment_method") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeDark,
                    contentColor = Color.White
                )
            ) {
                Text("Editar medio de pago", fontFamily = Poppins)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = BlueDark.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = BlueDark
        )
    }
}