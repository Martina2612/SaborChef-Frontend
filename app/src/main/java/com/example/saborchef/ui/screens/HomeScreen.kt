package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.models.TopRecetaResponse
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.components.BottomBar
import com.example.saborchef.ui.components.RecipeCarouselSection
import com.example.saborchef.ui.components.TopCarouselSection
import com.example.saborchef.viewmodel.HomeUiState
import com.example.saborchef.viewmodel.HomeViewModel
import com.example.saborchef.viewmodel.HomeViewModelFactory
import com.example.saborchef.ui.theme.OrangeDark

@Composable
fun HomeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val dm = remember { DataStoreManager(context) }
    val vm: HomeViewModel = viewModel(factory = HomeViewModelFactory(dm))
    val uiState by vm.uiState.collectAsState()

    when (uiState) {
        is HomeUiState.Loading -> FullScreenLoading()
        is HomeUiState.Error -> FullScreenError((uiState as HomeUiState.Error).message)
        is HomeUiState.Success -> {
            val data = uiState as HomeUiState.Success
            BaseHome(
                role = data.role, // Asegurate que HomeUiState.Success tenga `role: UserRole`
                topRecetas = data.topRecetas,
                ultimasRecetas = data.ultimasRecetas,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseHome(
    role: Rol,
    topRecetas: List<TopRecetaResponse>,
    ultimasRecetas: List<RecetaDetalleResponse>,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = logo_topbar),
                            contentDescription = "Logo",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                },
                modifier = Modifier.height(100.dp),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OrangeDark
                ),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        },
        bottomBar = {
            BottomBar(navController = navController, role = role)
        },
        floatingActionButton = {
            if (role != Rol.VISITANTE) {
                FloatingActionButton(onClick = { /* Navegar a CrearRecetaScreen */ }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            TopCarouselSection(
                title = "Top Recetas",
                items = topRecetas,
                onClick = { id -> navController.navigate("recipe/$id") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            RecipeCarouselSection(
                title = "Recetas publicadas últimamente",
                items = ultimasRecetas,
                onClick = { id -> navController.navigate("recipe/$id") }
            )
        }
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun FullScreenError(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}
