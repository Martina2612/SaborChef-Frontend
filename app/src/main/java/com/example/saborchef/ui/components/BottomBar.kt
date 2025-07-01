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

// 1) Modelo de Tab con matcher dinámico
sealed class TabItem(
    val route: String,
    val icon: ImageVector,
    val matcher: (String?) -> Boolean
) {
    object Home : TabItem("home", Icons.Default.Home, { it == "home" })

    object Videos : TabItem("cursos", Icons.Default.OndemandVideo, {
        it == "cursos" ||
                it == "mis_cursos" ||
                it?.startsWith("curso_detalle") == true ||
                it == "sedes_disponibles" ||
                it?.startsWith("sucursal_detalle") == true
    })


    object Bookmarks : TabItem("favs", Icons.Default.BookmarkBorder, { it == "favs" })

    object Search : TabItem("search", Icons.Default.Search, {
        it == "search" || it == "filter"
    })
}


// 2) Lista dinámica según rol
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

// 3) Composable de la barra
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
            val selected = tab.matcher(currentRoute)
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






