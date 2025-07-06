package com.example.saborchef.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.model.Rol
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier

// SIMPLIFICADO: Sin matcher complejo, como en tu otra rama
sealed class TabItem(
    val route: String,
    val icon: ImageVector
) {
    object Home : TabItem("simple_home", Icons.Default.Home)
    object Videos : TabItem("cursos", Icons.Default.OndemandVideo)
    object Bookmarks : TabItem("saved_recipes", Icons.Default.BookmarkBorder) // Cambiado a saved_recipes
    object Search : TabItem("search", Icons.Default.Search)
}

// Lista dinámica según rol
fun tabsForRole(role: Rol): List<TabItem> =
    when (role) {
        Rol.ALUMNO, Rol.USUARIO -> listOf(
            TabItem.Home,
            TabItem.Videos,
            TabItem.Bookmarks,
            TabItem.Search
        )
        else -> listOf(
            TabItem.Home,
            TabItem.Videos,
            TabItem.Search
        )
    }

// Composable de la barra con lógica de rutas relacionadas
@Composable
fun BottomBar(navController: NavController, role: Rol) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = BlueLight,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        tabsForRole(role).forEach { tab ->
            // Lógica mejorada: comparación directa + rutas relacionadas
            val selected = when (tab.route) {
                "simple_home" -> currentRoute == "simple_home"
                "cursos" -> currentRoute == "cursos" ||
                        currentRoute == "mis_cursos" ||
                        currentRoute?.startsWith("curso_detalle") == true ||
                        currentRoute == "sedes_disponibles" ||
                        currentRoute?.startsWith("sucursal_detalle") == true ||
                        currentRoute?.startsWith("mis_cursos_detalle") == true
                "search" -> currentRoute == "search" || currentRoute == "filter"
                "saved_recipes" -> currentRoute == "saved_recipes" || currentRoute == "favs" // Incluye ambas rutas
                else -> currentRoute == tab.route
            }

            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        tint = if (selected) OrangeDark else BlueLight
                    )
                },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(tab.route) {
                            // Limpia el stack hasta el destino inicial, manteniendo el estado
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}






