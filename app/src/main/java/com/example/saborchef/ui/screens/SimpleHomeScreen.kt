package com.example.saborchef.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.R.drawable.logo_topbar
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.ui.components.CustomLogoutDialog
import com.example.saborchef.ui.components.TopCarouselSection
import com.example.saborchef.ui.components.RecipeCarouselSection
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.OrangeLight
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.HomeUiState
import com.example.saborchef.viewmodel.HomeViewModel
import com.example.saborchef.viewmodel.HomeViewModelFactory
import com.example.saborchef.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleHomeScreen(
    nombre: String?,
    navController: NavController,
    role: Rol,
    dataStoreManager: DataStoreManager
) {
    android.util.Log.d("SimpleHomeScreen", "=== SIMPLE HOME CARGADA ===")
    android.util.Log.d("SimpleHomeScreen", "Nombre: $nombre")
    android.util.Log.d("SimpleHomeScreen", "Role: $role")
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // ViewModel para top y últimas
    val context = LocalContext.current
    val homeVm: HomeViewModel =
        viewModel(factory = HomeViewModelFactory(dataStoreManager))
    val uiState by homeVm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxSize(),  contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = logo_topbar),
                            contentDescription = "Logo",
                            modifier = Modifier.size(220.dp).padding(vertical = 10.dp)
                        )
                    }
                },
                navigationIcon = {
                    // Icono perfil siempre
                    if (role == Rol.USUARIO || role == Rol.ALUMNO) {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Mi perfil", tint = Color.White)
                    }}else{
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                        }
                    }
                },
                actions = {
                    // Logout para USUARIO/ALUMNO
                    if (role == Rol.USUARIO || role == Rol.ALUMNO) {
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(OrangeDark),
                modifier = Modifier.height(80.dp)
            )
        },
        bottomBar = { BottomBar(navController = navController, role = role) },
        floatingActionButton = {
            if (role != Rol.VISITANTE) {
                FloatingActionButton(
                    onClick = {navController.navigate("publishRecipe")},
                    containerColor = OrangeDark,
                    contentColor = OrangeLight,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir receta",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (uiState) {
            is HomeUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangeDark)
                }
            }
            is HomeUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = (uiState as HomeUiState.Error).message, color = MaterialTheme.colorScheme.error)
                }
            }
            is HomeUiState.Success -> {
                val data = uiState as HomeUiState.Success
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                ) {
                    Spacer(Modifier.height(20.dp))
                    // Sección Top Recetas
                    TopCarouselSection(
                        title = "Top Recetas",
                        items = data.topRecetas,
                        onClick = { id -> navController.navigate("recipe/$id") }
                    )
                    Spacer(Modifier.height(20.dp))

                    // Sección Últimas Recetas
                    RecipeCarouselSection(
                        title = "Recetas publicadas últimamente",
                        items = data.ultimasRecetas,
                        onClick = { id -> navController.navigate("recipe/$id") }
                    )
                }
            }
        }
    }

    // Diálogo de logout
    if (showLogoutDialog) {
        CustomLogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                scope.launch {
                    dataStoreManager.clearUserData()
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            },
            onCancel = { showLogoutDialog = false },
            onDismiss = { showLogoutDialog = false }
        )
    }
}
