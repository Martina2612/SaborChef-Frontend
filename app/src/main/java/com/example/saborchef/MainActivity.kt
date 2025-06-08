// MainActivity.kt
package com.example.saborchef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.saborchef.network.AuthRepository
import com.example.saborchef.network.NewPasswordRequest
import com.example.saborchef.network.PasswordResetRequest
import com.example.saborchef.ui.screens.*
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.LoginViewModel
import com.example.saborchef.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface {
                    val navController = rememberNavController()
                    val searchViewModel: SearchViewModel = viewModel()
                    val loginViewModel: LoginViewModel = viewModel()

                    // Estados compartidos
                    var alias by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    val loginState by loginViewModel.loginState.collectAsState()

                    var recoveryEmail by remember { mutableStateOf("") }
                    var recoveryPassword by remember { mutableStateOf("") }
                    var isLoading by remember { mutableStateOf(false) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    val scope = rememberCoroutineScope()
                    var resetTimerTrigger by remember { mutableStateOf(0) }

                    NavHost(navController = navController, startDestination = "splash") {

                        // --- Splash / Welcome / Auth ---
                        composable("splash") {
                            SplashScreen(navController)
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
                                onLogin    = { navController.navigate("login") },
                                onRegister = { navController.navigate("register") },
                                onBack     = { navController.popBackStack() }
                            )
                        }

                        // --- Login ---
                        composable("login") {
                            LoginScreen(
                                aliasValue      = alias,
                                passwordValue   = password,
                                loginState      = loginState,
                                onAliasChange   = { alias = it },
                                onPasswordChange= { password = it },
                                onLoginClick    = { a, p -> loginViewModel.login(a, p) },
                                onBack          = { navController.popBackStack() },
                                onLoginSuccess  = {
                                    navController.navigate("home") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                },
                                onForgotPassword = {
                                    navController.navigate("password_email")
                                },
                                onRegister      = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // --- Register (datos) ---
                        composable("register") {
                            RegisterScreen(
                                navController = navController,
                                onRegisterSuccess = { email ->
                                    navController.navigate("verify_registration/$email") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Verify code para REGISTRO ---
                        composable(
                            route = "verify_registration/{email}",
                            arguments = listOf(navArgument("email") {
                                type = NavType.StringType
                            })
                        ) { back ->
                            val email = back.arguments?.getString("email") ?: ""
                            VerificationCodeScreen(
                                email = email,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    navController.navigate("successful_register") {
                                        popUpTo("verify_registration/$email") { inclusive = true }
                                    }
                                },
                                onResendCode = {
                                    resetTimerTrigger++
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            // reenvío de código de registro, ajusta si tu endpoint es otro
                                            AuthRepository.sendPasswordResetEmailRaw(
                                                PasswordResetRequest(email)
                                            )
                                        } catch (_: Exception) {}
                                    }
                                },
                                resetTrigger = resetTimerTrigger
                            )
                        }

                        // --- Pantalla de Éxito de REGISTRO ---
                        composable("successful_register") {
                            SuccessfulRegisterScreen(
                                onContinue = {
                                    navController.navigate("home") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Recuperación de contraseña: email ---
                        composable("password_email") {
                            PasswordEmailScreen(
                                email         = recoveryEmail,
                                onEmailChange = { recoveryEmail = it },
                                isLoading     = isLoading,
                                errorMessage  = errorMessage,
                                onSubmit      = {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val resp = AuthRepository
                                                .sendPasswordResetEmailRaw(PasswordResetRequest(recoveryEmail))
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                if (resp.isSuccessful) {
                                                    navController.navigate("verify_recovery/$recoveryEmail")
                                                } else {
                                                    errorMessage = "Error HTTP ${resp.code()}"
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                errorMessage = "Excepción: ${e.message}"
                                            }
                                        }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- Verify code para RECUPERACIÓN ---
                        composable(
                            route = "verify_recovery/{email}",
                            arguments = listOf(navArgument("email") {
                                type = NavType.StringType
                            })
                        ) { back ->
                            val email = back.arguments?.getString("email") ?: ""
                            VerificationCodeScreen(
                                email = email,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    navController.navigate("password_new/$email")
                                },
                                onResendCode = {
                                    resetTimerTrigger++
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            AuthRepository.sendPasswordResetEmailRaw(
                                                PasswordResetRequest(email)
                                            )
                                        } catch (_: Exception) {}
                                    }
                                },
                                resetTrigger = resetTimerTrigger
                            )
                        }

                        // --- Nueva contraseña (recovery) ---
                        composable(
                            route = "password_new/{email}",
                            arguments = listOf(navArgument("email") {
                                type = NavType.StringType
                            })
                        ) { back ->
                            val email = back.arguments?.getString("email") ?: ""
                            PasswordNewScreen(
                                password         = recoveryPassword,
                                onPasswordChange = { recoveryPassword = it },
                                isLoading        = isLoading,
                                errorMessage     = errorMessage,
                                onSubmit         = {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val r = AuthRepository.resetPassword(
                                                NewPasswordRequest(email, recoveryPassword)
                                            )
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                if (r.success) {
                                                    navController.navigate("password_success")
                                                } else {
                                                    errorMessage = r.message
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                errorMessage = "Excepción: ${e.message}"
                                            }
                                        }
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- Recovery success ---
                        composable("password_success") {
                            PasswordSuccessScreen(
                                onBackToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Pantallas principales ---
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("search") {
                            SearchScreen(navController, viewModel = searchViewModel)
                        }
                        composable("filter") {
                            FilterScreen(navController, viewModel = searchViewModel)
                        }
                        composable(
                            route = "recipe/{id}",
                            arguments = listOf(navArgument("id") {
                                type = NavType.StringType
                            })
                        ) { back ->
                            val id = back.arguments?.getString("id") ?: "0"
                            RecipeDetailScreen(
                                recipeId = id,
                                onBack   = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
