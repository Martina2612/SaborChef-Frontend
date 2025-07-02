package com.example.saborchef.ui.screens

import android.util.Log
import com.example.saborchef.ui.components.DetallesTabContent
import com.example.saborchef.ui.components.BottomBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import com.example.saborchef.model.CursoInscripto
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.components.AsistenciaTabContent
import com.example.saborchef.ui.components.CronogramaTabContent
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.viewmodel.ClasesViewModel
import com.example.saborchef.viewmodel.CursoViewModel

@Composable
fun MisCursosDetalleScreen(curso: CursoInscripto, navController: NavController) {
    val tabs = listOf("Detalles", "Cronograma", "Asistencia")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val viewModel: CursoViewModel = viewModel()
    val cursoCompleto by viewModel.cursoDetalle.collectAsState()
    val context = LocalContext.current
    val clasesViewModel: ClasesViewModel = viewModel()
    val clases by clasesViewModel.clases.collectAsState()
    val mostrarScanner = remember { mutableStateOf(false) }



    LaunchedEffect(Unit) {
        Log.d("DEBUG", "ID CRONOGRAMA: ${curso.idCronograma}")
        viewModel.getCursoPorId(curso.idCurso)
        clasesViewModel.cargarClasesPorCronograma(context,curso.idCronograma)
    }


    Scaffold(
        bottomBar = {
            BottomBar(navController = navController, role = Rol.ALUMNO)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Espacio blanco arriba (status bar)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(Color.White)
            )

            // Header naranja
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Orange)
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = " ${curso.nombreCurso}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = contentPadding.calculateBottomPadding())
            ) {
                Image(
                    painter = rememberAsyncImagePainter(curso.imagenUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            selectedContentColor = Orange,
                            unselectedContentColor = Color.Gray
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> DetallesTabContent(curso,navController)
                    1 -> {
                        cursoCompleto?.let {
                            CronogramaTabContent(clases = clases)
                        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Orange)
                        }
                    }
                    2 -> AsistenciaTabContent(
                        clases = clases,
                        viewModel = clasesViewModel,
                        context = context,
                        mostrarScanner = mostrarScanner
                    )


                }
            }
        }
    }
}


