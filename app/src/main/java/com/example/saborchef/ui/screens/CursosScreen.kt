package com.example.saborchef.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.Nivel
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.ui.components.SearchBar
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.viewmodel.CursoUiState
import com.example.saborchef.viewmodel.CursoViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CursosScreen(
    navController: NavController,
    viewModel: CursoViewModel = viewModel(),
    userRole: Rol = Rol.ALUMNO
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    var actualRol by remember { mutableStateOf<Rol?>(null) }

    // Recupera el rol guardado
    LaunchedEffect(Unit) {
        dataStoreManager.role.collectLatest {
            actualRol = it?.let { valor -> Rol.valueOf(valor) }
        }
    }

    // Mostrar pantalla especial si el usuario es USUARIO
    if (actualRol == Rol.USUARIO) {
        MostrarPantallaSerAlumno(
            onQuieroSerAlumno = { /* TODO: implementar conversión */ },
            onContinuar = { }
        )
        return
    }

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {},
                    onFilterClick = {}
                )
            }
        },
        bottomBar = {
            BottomBar(navController = navController, role = userRole)
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is CursoUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CursoUiState.Error -> {
                Box(
                    Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is CursoUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.cursos) { curso ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(curso.imagenUrl),
                                    contentDescription = curso.nombre,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = curso.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlueDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Chef ${curso.chef}", fontSize = 16.sp, color = Color.Gray)
                                Text(text = nivelToText(curso.nivel), fontSize = 16.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {},
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangeDark),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = curso.modalidad, color = Color.White, fontWeight = FontWeight.Medium)
                                    }
                                    TextButton(onClick = {}, modifier = Modifier.weight(1f)) {
                                        Text(text = "Ver", color = OrangeDark, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MostrarPantallaSerAlumno(
    onQuieroSerAlumno: () -> Unit,
    onContinuar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ups! Debes ser Alumno para poder inscribirte a un curso.",
            color = BlueLight,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onQuieroSerAlumno,
            colors = ButtonDefaults.buttonColors(containerColor = OrangeDark),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Quiero ser Alumno", color = Color.White)
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onContinuar,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeDark)
        ) {
            Text("Continuar viendo cursos")
        }
    }
}

private fun nivelToText(nivel: Nivel): String {
    return when (nivel) {
        Nivel.PRINCIPIANTE -> "Principiante"
        Nivel.INTERMEDIO -> "Intermedio"
        Nivel.AVANZADO -> "Avanzado"
    }
}



