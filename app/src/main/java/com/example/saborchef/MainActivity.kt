package com.example.saborchef

import com.example.saborchef.ui.screens.VerificationCodeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.saborchef.ui.screens.*
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.SearchViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface {
                    val navController = rememberNavController()
                    val searchViewModel: SearchViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(navController = navController)
                        }

                        composable("welcome") {
                            WelcomeScreen(
                                navController = navController,
                                onContinueAsUser = { navController.navigate("auth") },
                                onContinueAsGuest = { navController.navigate("home") }
                            )
                        }

                        composable("auth") {
                            AuthScreen(
                                onLogin = { /* luego podés poner navController.navigate("home") */ },
                                onRegister = { navController.navigate("register") },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onBack = { navController.popBackStack() },
                                onRegisterSuccess = { email ->
                                    navController.navigate("verify/$email")
                                },
                                onLoginClick = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "verify/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            VerificationCodeScreen(
                                email = email,
                                onBack = { navController.popBackStack() },
                                onNext = { navController.navigate("success") },
                                onResendCode = {
                                    // Aquí podrías reutilizar el viewModel para reenviar el código
                                }
                            )
                        }

                        composable("success") {
                            SuccessfulRegisterScreen(
                                onContinue = {
                                    navController.navigate("home") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
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

                        composable("home") {
                            HomeScreen(navController = navController)
                        }

                        composable("search") {
                            SearchScreen(
                                navController = navController,
                                viewModel = searchViewModel
                            )
                        }

                        composable("filter") {
                            FilterScreen(
                                navController = navController,
                                viewModel = searchViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
