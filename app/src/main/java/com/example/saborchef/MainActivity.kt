package com.example.saborchef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.saborchef.ui.screens.AuthScreen
import com.example.saborchef.ui.screens.FilterScreen
import com.example.saborchef.ui.screens.HomeScreen
import com.example.saborchef.ui.screens.PublishRecipeScreen
import com.example.saborchef.ui.screens.RecipeDetailScreen
import com.example.saborchef.ui.screens.RegisterScreen
import com.example.saborchef.ui.screens.SearchScreen
import com.example.saborchef.ui.screens.SplashScreen
import com.example.saborchef.ui.screens.WelcomeScreen
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.PublishRecipeViewModel
import com.example.saborchef.viewmodel.SearchViewModel
import com.example.saborchef.viewmodel.SubmitState
import androidx.lifecycle.ViewModelProvider


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
                                // al acabar el splash, vamos a "publish"
                                onTimeout = { navController.navigate("publish") }
                            )
                        }

                        // … tus otras rutas …

                        composable("publish") {
                            // Obtenemos ContentResolver del contexto
                            val context = LocalContext.current
                            // Creamos el VM in‐place pasando ContentResolver
                            val publishVm: PublishRecipeViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        @Suppress("UNCHECKED_CAST")
                                        return PublishRecipeViewModel(context.contentResolver) as T
                                    }
                                }
                            )

                            // Observamos el estado de envío
                            val submitStateState = publishVm.submitState.collectAsState()
                            val submitState = submitStateState.value

                            // Mostramos la pantalla
                            PublishRecipeScreen(
                                navController = navController,
                                onSubmit = { data ->
                                    publishVm.submitRecipe(data)
                                }
                            )

                            // Reaccionamos al resultado
                            LaunchedEffect(submitState) {
                                when (submitState) {
                                    SubmitState.Loading -> {
                                        // aquí podrías mostrar un loader
                                    }
                                    SubmitState.Success -> {
                                        // navegación tras éxito, p.ej. volver al home
                                        navController.navigate("home") {
                                            popUpTo("publish") { inclusive = true }
                                        }
                                    }
                                    is SubmitState.Error -> {
                                        // mostrar Snackbar/Toast con mensaje
                                        val msg = (submitState as SubmitState.Error).message
                                        // … tu lógica de error …
                                    }
                                    else -> {}
                                }
                            }
                        }

                        composable("home") {
                            HomeScreen(navController)
                        }
            /*SaborChefTheme {
                Surface {
                    val navController = rememberNavController()
                    val searchViewModel: SearchViewModel = viewModel()
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
                                onContinueAsGuest = { navController.navigate("home")}
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

                        composable("home") {
                            HomeScreen(
                                navController = navController
                            )
                        }

                        composable("search") {
                            SearchScreen(navController = navController, viewModel = searchViewModel)
                        }
                        composable("filter") {
                            FilterScreen(navController = navController, viewModel = searchViewModel)
                        }*/

                    }
                }
            }
        }
    }
}
