package com.example.saborchef.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    userRole: Rol = Rol.VISITANTE // CAMBIO: Default a VISITANTE en lugar de ALUMNO
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    var actualRol by remember { mutableStateOf<Rol?>(null) }

    // CAMBIO: Recupera el rol y carga cursos de manera diferente según el rol
    LaunchedEffect(Unit) {
        dataStoreManager.role.collectLatest { roleString ->
            actualRol = roleString?.let { Rol.valueOf(it) } ?: Rol.VISITANTE

            // Solo cargar cursos si tenemos un rol definido
            actualRol?.let { rol ->
                when (rol) {
                    Rol.VISITANTE -> {
                        // Para visitantes, usar un ID especial o -1
                        viewModel.fetchCursos(-1L) // Backend debe manejar este caso
                    }
                    Rol.USUARIO, Rol.ALUMNO -> {
                        // Para usuarios autenticados, usar su userId real
                        dataStoreManager.userId.collectLatest { id ->
                            if (id != null) {
                                viewModel.fetchCursos(id)
                            }
                        }
                    }
                    else -> {
                        viewModel.fetchCursos(-1L)
                    }
                }
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    // CAMBIO: Usar el rol real en lugar del parámetro hardcodeado
    val rolParaBottomBar = actualRol ?: userRole

    Scaffold(
        topBar = {
            var expanded by remember { mutableStateOf(false) }

            // Filtrar nombres de cursos que coincidan con el texto escrito
            val suggestions = when (val state = uiState) {
                is CursoUiState.Success -> {
                    state.cursos
                        .map { it.nombre }
                        .filter { it.contains(searchQuery, ignoreCase = true) }
                        .distinct()
                        .take(5)
                }
                else -> emptyList()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        expanded = it.isNotBlank()
                    },
                    onSearch = {
                        expanded = false
                    },
                    onFilterClick = {},
                    placeholderText = "Busca un curso aquí"
                )

                if (expanded && suggestions.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF3F3F3))
                            .padding(top = 4.dp)
                    ) {
                        suggestions.forEach { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .clickable {
                                        searchQuery = suggestion
                                        expanded = false
                                    },
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {

            // CAMBIO: Usar el rol real del usuario
            BottomBar(navController = navController, role = rolParaBottomBar)

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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error al cargar cursos",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                // Intentar recargar
                                actualRol?.let { rol ->
                                    when (rol) {
                                        Rol.VISITANTE -> viewModel.fetchCursos(-1L)
                                        else -> {
                                            // Para usuarios autenticados, necesitaríamos el userId
                                            viewModel.fetchCursos(-1L)
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            is CursoUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.cursos.filter { it.nombre.contains(searchQuery, ignoreCase = true) }) { curso ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(curso.imagenUrl),
                                    contentDescription = curso.nombre,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = curso.nombre,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BlueDark
                                        )
                                        Text(
                                            text = "Chef ${curso.chef}",
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = nivelToText(curso.nivel),
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {},
                                            colors = ButtonDefaults.buttonColors(containerColor = OrangeDark),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = curso.modalidad,
                                                color = Color.White,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }
                                        TextButton(
                                            onClick = {
                                                // CAMBIO: Solo ALUMNO puede ver detalles, otros ven pantalla para upgrade
                                                if (rolParaBottomBar == Rol.ALUMNO) {
                                                    // Solo alumnos pueden ver el detalle completo
                                                    navController.navigate("curso_detalle/${curso.idCurso}")
                                                } else {
                                                    // VISITANTE y USUARIO ven la misma pantalla de upgrade
                                                    navController.navigate("upgrade_to_student")
                                                }
                                            }
                                        ) {
                                            Text(
                                                text = "Ver",
                                                color = OrangeDark,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 16.sp
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
}

private fun nivelToText(nivel: Nivel): String {
    return when (nivel) {
        Nivel.PRINCIPIANTE -> "Principiante"
        Nivel.INTERMEDIO -> "Intermedio"
        Nivel.AVANZADO -> "Avanzado"
    }
}



