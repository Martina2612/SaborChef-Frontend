package com.example.saborchef.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.model.Nivel
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.components.SearchBar
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.viewmodel.CursoUiState
import com.example.saborchef.viewmodel.CursoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CursosScreen(
    navController: NavController,
    viewModel: CursoViewModel = viewModel(),
    userRole: Rol = Rol.ALUMNO // Puedes pasar el rol del usuario desde donde llames esta función
) {
    val uiState by viewModel.uiState.collectAsState()

    // Estado para la búsqueda
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
                    onSearch = {
                        // Aquí puedes implementar la lógica de búsqueda
                        // viewModel.searchCursos(searchQuery)
                    },
                    onFilterClick = {
                        // Aquí puedes implementar la lógica de filtros
                    }
                )
            }
        },
        bottomBar = {
            BottomBar(
                navController = navController,
                role = userRole
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is CursoUiState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CursoUiState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Imagen del curso
                                Image(
                                    painter = rememberAsyncImagePainter(curso.imagenUrl),
                                    contentDescription = curso.nombre,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Título del curso
                                Text(
                                    text = curso.nombre,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BlueDark
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Chef
                                Text(
                                    text = "Chef ${curso.chef}",
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )

                                // Nivel
                                Text(
                                    text = nivelToText(curso.nivel),
                                    fontSize = 16.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Botones
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {},
                                        colors = ButtonDefaults.buttonColors(containerColor = OrangeDark),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = curso.modalidad,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            // navController.navigate("curso_detalle/${curso.id}")
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Ver",
                                            color = OrangeDark,
                                            fontWeight = FontWeight.Medium
                                        )
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

private fun nivelToText(nivel: Nivel): String {
    return when (nivel) {
        Nivel.PRINCIPIANTE -> "Principiante"
        Nivel.INTERMEDIO -> "Intermedio"
        Nivel.AVANZADO -> "Avanzado"
    }
}



