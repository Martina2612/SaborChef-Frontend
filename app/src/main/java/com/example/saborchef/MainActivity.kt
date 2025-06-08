package com.example.saborchef

import android.net.Uri
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
import com.example.saborchef.viewmodel.*
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
                    val registerViewModel: RegisterViewModel = viewModel()
                    val sharedAlumnoViewModel: SharedAlumnoViewModel = viewModel()
                    val scope = rememberCoroutineScope()

                    var alias by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var recoveryEmail by remember { mutableStateOf("") }
                    var recoveryPassword by remember { mutableStateOf("") }
                    var isLoading by remember { mutableStateOf(false) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    var resetTimerTrigger by remember { mutableIntStateOf(0) }
                    val loginState by loginViewModel.loginState.collectAsState()

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("welcome") {
                            WelcomeScreen(
                                navController = navController,
                                onContinueAsUser = { navController.navigate("auth") },
                                onContinueAsGuest = {
                                    navController.navigate("simple_home")
                                }
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
                                    navController.navigate("simple_home") {
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
                                navController = navController,
                                sharedAlumnoViewModel = sharedAlumnoViewModel,
                                onRegisterSuccess = { email ->
                                    navController.navigate("verify_registration/$email") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("upload_dni") {
                            DniUploadScreen(
                                onBack = { navController.popBackStack() },
                                onFinish = { frontUri: Uri?, backUri: Uri?, tramite: String ->
                                    sharedAlumnoViewModel.setDniInfo(frontUri, backUri, tramite)
                                    navController.navigate("add_payment")
                                }
                            )
                        }
                        composable("add_payment") {
                            AddPaymentScreen(
                                sharedAlumnoViewModel = sharedAlumnoViewModel,
                                navController = navController,
                                viewModel = registerViewModel
                            )
                        }
                        composable("verify_registration/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
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
                                        AuthRepository.sendPasswordResetEmailRaw(PasswordResetRequest(email))
                                    }
                                },
                                resetTrigger = resetTimerTrigger
                            )
                        }
                        composable("successful_register") {
                            SuccessfulRegisterScreen(
                                onContinue = {
                                    alias = sharedAlumnoViewModel.alias
                                    navController.navigate("simple_home") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("password_email") {
                            PasswordEmailScreen(
                                email = recoveryEmail,
                                onEmailChange = { recoveryEmail = it },
                                isLoading = isLoading,
                                errorMessage = errorMessage,
                                onSubmit = {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val resp = AuthRepository.sendPasswordResetEmailRaw(
                                                PasswordResetRequest(recoveryEmail)
                                            )
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
                        composable("verify_recovery/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            VerificationCodeScreen(
                                email = email,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    navController.navigate("password_new/$email")
                                },
                                onResendCode = {
                                    resetTimerTrigger++
                                    scope.launch(Dispatchers.IO) {
                                        AuthRepository.sendPasswordResetEmailRaw(
                                            PasswordResetRequest(email)
                                        )
                                    }
                                },
                                resetTrigger = resetTimerTrigger
                            )
                        }
                        composable("password_new/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            PasswordNewScreen(
                                password = recoveryPassword,
                                onPasswordChange = { recoveryPassword = it },
                                isLoading = isLoading,
                                errorMessage = errorMessage,
                                onSubmit = {
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val resp = AuthRepository.resetPassword(
                                                NewPasswordRequest(email, recoveryPassword)
                                            )
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                if (resp.success) {
                                                    navController.navigate("password_success")
                                                } else {
                                                    errorMessage = resp.message
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
                        composable("password_success") {
                            PasswordSuccessScreen(
                                onBackToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("simple_home") {
                            SimpleHomeScreen(nombre = alias.ifBlank { null })
                        }
                        composable("search") {
                            SearchScreen(navController, viewModel = searchViewModel)
                        }
                        composable("filter") {
                            FilterScreen(navController, viewModel = searchViewModel)
                        }
                        composable("recipe/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: "0"
                            RecipeDetailScreen(
                                recipeId = id,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
