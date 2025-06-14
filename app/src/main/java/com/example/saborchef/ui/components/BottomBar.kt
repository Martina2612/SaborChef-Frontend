package com.example.saborchef.ui.components;

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.model.Rol
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape

// 1) Modelo de Tab
sealed class TabItem(val route: String, val icon: ImageVector) {
    object Home      : TabItem("home", Icons.Default.Home)
    object Videos    : TabItem("cursos", Icons.Default.OndemandVideo)
    object Bookmarks : TabItem("favs", Icons.Default.BookmarkBorder)
    object Search    : TabItem("search", Icons.Default.Search)
}

// 2) Lista dinámica según rol
fun tabsForRole(role: Rol): List<TabItem> =
    when(role) {
        Rol.ALUMNO,
        Rol.USUARIO -> listOf(
            TabItem.Home,
            TabItem.Videos,
            TabItem.Bookmarks,
            TabItem.Search
        )
        // visitante no ve “Bookmarks”
        else -> listOf(
            TabItem.Home,
            TabItem.Videos,
            TabItem.Search
        )
    }

// 3) Composable de la barra
@Composable
fun BottomBar(
    navController: NavController,
    role: Rol
) {
    val items = tabsForRole(role)
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route

    BottomNavigation(
        backgroundColor = Color.White,
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        items.forEach { tab ->
            val selected = tab.route == currentRoute
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.route,
                        tint = if (selected) OrangeDark else BlueLight
                    )
                },
                selected = selected,
                onClick = {
                    if (tab.route != currentRoute) {
                        navController.navigate(tab.route) {
                            // Poppeo todo hasta "home", pero sin eliminar "home" (inclusive = false)
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                }
            )
        }
    }
}



