package com.example.saborchef

import androidx.navigation.navArgument
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.saborchef.ui.screens.*
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.LoginViewModel
import com.example.saborchef.viewmodel.LoginState
import com.example.saborchef.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface {
                    val navController = rememberNavController()
                    val searchViewModel: SearchViewModel = viewModel()
                    val loginViewModel: LoginViewModel = viewModel()

                    var alias by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    val loginState by loginViewModel.loginState.collectAsState()

                    NavHost(navController = navController, startDestination = "splash") {

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
                                onLogin = { navController.navigate("login") },
                                onRegister = { navController.navigate("register") },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                aliasValue = alias,
                                passwordValue = password,
                                loginState = loginState,
                                onAliasChange = { alias = it },
                                onPasswordChange = { password = it },
                                onLoginClick = { a, p -> loginViewModel.login(a, p) },
                                onBack = { navController.popBackStack() },
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                },
                                onForgotPassword = {
                                    navController.navigate("password_email")
                                },
                                onRegister = {
                                    navController.navigate("register")
                                }
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

                        composable("password_email") {
                            PasswordEmailScreen(
                                email = "",
                                onEmailChange = {},
                                isLoading = false,
                                errorMessage = null,
                                onSubmit = {},
                                onBack = { navController.popBackStack() }
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
                                onResendCode = {}
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

                        composable(
                            route = "recipe/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            RecipeDetailScreen(
                                recipeId = backStackEntry.arguments?.getString("id") ?: "0",
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
