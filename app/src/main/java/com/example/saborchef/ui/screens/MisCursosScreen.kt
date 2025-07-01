package com.example.saborchef.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.viewmodel.CursoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.components.BottomBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.background
import androidx.compose.ui.layout.ContentScale
import com.example.saborchef.ui.components.CursoCard
import com.example.saborchef.viewmodel.MisCursosViewModel
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCursosScreen(navController: NavController) {
    val context = LocalContext.current

    val viewModel: MisCursosViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MisCursosViewModel(context) as T
        }
    })

    val cursos by viewModel.cursos.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    val cursosCompletos = cursos.filter { it.finalizado }
    val cursosEnProgreso = cursos.filter { !it.finalizado }

    Scaffold(
        bottomBar = { BottomBar(navController, Rol.ALUMNO) },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 0.dp)

    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Orange)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Orange)
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SaborChef",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                }

                if (cursosCompletos.isNotEmpty()) {
                    item {
                        Text(
                            "Cursos Completos",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF5B5B8E)
                        )
                    }

                    items(cursosCompletos) { curso ->
                        CursoCard(curso)
                    }
                }

                if (cursosEnProgreso.isNotEmpty()) {
                    item {
                        Text(
                            "Cursos en Progreso",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF5B5B8E)
                        )
                    }

                    items(cursosEnProgreso) { curso ->
                        CursoCard(curso, mostrarBarraProgreso = true, onClick = {
                            val cursoJson = Uri.encode(Gson().toJson(curso))
                            navController.navigate("mis_cursos_detalle/$cursoJson")
                        })
                    }

                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("cursos") },
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) {
                        Text("Descubre más cursos aquí!", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}



