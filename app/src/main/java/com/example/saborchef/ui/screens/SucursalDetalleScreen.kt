package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.viewmodel.SedeViewModel

@Composable
fun SucursalDetalleScreen(
    sedeId: Long,
    navController: NavController
) {
    val sedeViewModel: SedeViewModel = viewModel()
    val sedeState by sedeViewModel.sede.collectAsState()

    LaunchedEffect(sedeId) {
        sedeViewModel.obtenerSedePorId(sedeId)
    }

    sedeState?.let { sede ->
        Scaffold(
            bottomBar = {
                // Puedes agregar BottomBar si querés
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Sucursal ${sede.nombreSede}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Orange,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Image(
                    painter = rememberAsyncImagePainter(sede.imagenUrl ?: "https://via.placeholder.com/400x200"),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("📍 Dirección: ${sede.direccionSede}", color = BlueDark)
                Text("📞 Teléfono: ${sede.telefonoSede}", color = BlueDark)
                Text("📧 Email: ${sede.mailSede}", color = BlueDark)
                Text("📱 WhatsApp: ${sede.whatsapp}", color = BlueDark)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text("Volver")
                    }
                    Button(
                        onClick = {
                            // Lógica de confirmación
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) {
                        Text("Confirmar", color = Color.White)
                    }
                }
            }
        }
    } ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Orange)
        }
    }
}

