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
import com.example.saborchef.ui.screens.FavoriteRecipesScreen
import com.example.saborchef.ui.screens.FilterScreen
import com.example.saborchef.ui.screens.RecipeDetailScreen
import com.example.saborchef.ui.screens.SearchScreen
import com.example.saborchef.ui.screens.SearchUiState
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
                                navController = navController,
                                // dentro de tu SplashScreen deberías tener algo como:
                                // LaunchedEffect(Unit) { delay(2000); navController.navigate("welcome") }
                            )
                        }
                        composable("welcome") {
                            WelcomeScreen(
                                navController = navController,
                                onContinueAsUser = { /* TODO: navegar a Login o Home */ },
                                onContinueAsGuest = { /* TODO: navegar como invitado */ }
                            )
                        }

                    }
                }
            }

        }
    }
}
