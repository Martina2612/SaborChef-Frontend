package com.example.saborchef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.saborchef.ui.screens.AuthScreen
import com.example.saborchef.ui.screens.FavoriteRecipesScreen
import com.example.saborchef.ui.screens.FilterScreen
import com.example.saborchef.ui.screens.RecipeDetailScreen
import com.example.saborchef.ui.screens.RegisterScreen
import com.example.saborchef.ui.screens.SearchScreen
import com.example.saborchef.ui.screens.SplashScreen
import com.example.saborchef.ui.screens.WelcomeScreen
import com.example.saborchef.ui.theme.SaborChefTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                navController = navController
                            )
                        }
                        composable("welcome") {
                            WelcomeScreen(
                                navController = navController,
                                onContinueAsUser = {  navController.navigate("auth") },
                                onContinueAsGuest = { }
                            )
                        }

                        composable(
                            route = "recipe/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            RecipeDetailScreen(
                                recipeId = backStackEntry.arguments?.getString("id") ?: "0",
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("auth") {
                            AuthScreen(
                                onLogin    = { /* navegar tras login, p.ej. navController.navigate("home") */ },
                                onRegister = { navController.navigate("register")},
                                onBack     = { navController.popBackStack() }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onBack = { navController.popBackStack() },
                                onRegisterSuccess = {},
                                onLoginClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
