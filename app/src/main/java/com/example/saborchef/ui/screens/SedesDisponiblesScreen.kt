package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.Cronograma
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.components.BottomBar
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.net.URLEncoder
import java.nio.charset.StandardCharsets



@Composable
fun SedesDisponiblesScreen(
    cronogramas: List<Cronograma>,
    onVolver: () -> Unit,
    navController: NavController
) {
    var rol by remember { mutableStateOf<Rol?>(null) }
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }


    // Recuperar el rol desde DataStore
    LaunchedEffect(Unit) {
        dataStoreManager.role.collect { storedRole ->
            println("ROL RECUPERADO: $storedRole") // Logcat debug
            rol = storedRole?.let { Rol.valueOf(it) }
        }
    }

    if (rol == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Orange)
        }
        return
    }


    Scaffold(
        bottomBar = {
            BottomBar(navController = navController, role = rol!!)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Sucursales disponibles",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Orange,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cronogramas) { cronograma ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF4E5))
                            .clickable {
                                navController.navigate(
                                    "sucursal_detalle/${cronograma.sede.idSede}/${cronograma.idCronograma}/true"
                                )
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Sucursal ${cronograma.sede.nombreSede}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlueDark
                                )
                                Text(
                                    text = "Vacantes: ${cronograma.vacantesDisponibles}",
                                    color = OrangeDark,
                                    fontSize = 14.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Ir",
                                tint = OrangeDark
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVolver,
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver", color = Color.White)
            }
        }
    }
}


