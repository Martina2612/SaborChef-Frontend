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
import com.example.saborchef.network.AuthRepository
import com.example.saborchef.network.PasswordResetRequest
import com.example.saborchef.network.NewPasswordRequest
import com.example.saborchef.ui.screens.*
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.LoginState
import com.example.saborchef.viewmodel.LoginViewModel
import com.example.saborchef.viewmodel.SearchViewModel
import com.example.saborchef.viewmodel.StudentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.navArgument
import com.example.saborchef.models.AlumnoActualizarDTO
import com.example.saborchef.models.RegistrationResult
import com.example.saborchef.models.StudentRegistrationData
import com.example.saborchef.network.ConvertToStudentRequest
import com.example.saborchef.viewmodel.StudentUiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface {
                    val navController = rememberNavController()
                    val searchViewModel: SearchViewModel = viewModel()
                    val loginViewModel: LoginViewModel = viewModel()
                    val studentVm: StudentViewModel = viewModel()
                    val uiState by studentVm.uiState.collectAsState()

                    var alias by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    val loginState by loginViewModel.loginState.collectAsState()

                    var recoveryEmail by remember { mutableStateOf("") }
                    var recoveryPassword by remember { mutableStateOf("") }
                    var isLoading by remember { mutableStateOf(false) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    val scope = rememberCoroutineScope()


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
                                onBack = { "auth" },
                                onRegisterSuccess = { result: RegistrationResult ->
                                    if (result.userRole == "ALUMNO") {
                                        studentVm.initWith(result)
                                    }
                                    navController.navigate("verify/${result.email}/${result.userRole}")
                                },
                                onLoginClick = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route = "verify/{email}/{role}",
                            arguments = listOf(
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType }
                            )
                        ) { backStack ->
                            val email = backStack.arguments!!.getString("email")!!
                            val role = backStack.arguments!!.getString("role")!!

                            VerificationCodeScreen(
                                email = email,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    if (role == "ALUMNO") {
                                        navController.navigate("student_payment") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("login") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    }
                                },
                                onResendCode = {}
                            )
                        }

                        composable("student_payment") {
                            AddPaymentScreen { number, type, expiry, cvv ->
                                studentVm.updatePayment(number, type, expiry, cvv)
                                navController.navigate("student_dni")
                            }
                        }

                        composable("student_dni") {
                            DniUploadScreen(
                                onBack = { navController.popBackStack() },
                                onFinish = { frontUri, backUri, tramite ->
                                    // Asegurar que frontUri y backUri no sean null antes de subir
                                    if (frontUri != null && backUri != null) {
                                        scope.launch {
                                            try {
                                                val (frontUrl, backUrl) = withContext(Dispatchers.IO) {
                                                    AuthRepository.uploadDniFiles(frontUri, backUri, this@MainActivity)
                                                }
                                                studentVm.updateDniUrls(frontUrl, backUrl, tramite)
                                                navController.navigate("student_success")
                                            } catch (e: Exception) {
                                                errorMessage = "Error subiendo DNI: ${e.message}"
                                            }
                                        }
                                    } else {
                                        errorMessage = "Debés subir ambas caras del DNI."
                                    } }) }
                        composable("student_success") {
                            RegistrationSuccessScreen(
                                isLoading    = (uiState == StudentUiState.Loading),
                                errorMessage = (uiState as? StudentUiState.Error)?.message ?: errorMessage,
                                isSuccess    = uiState is StudentUiState.Success,
                                onFinalize   = { studentVm.convertToStudent() },
                                onSuccess    = {
                                    navController.navigate("home") {
                                        popUpTo("register") { inclusive = true }
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
                                            val response = AuthRepository.sendPasswordResetEmailRaw(
                                                PasswordResetRequest(recoveryEmail)
                                            )
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                if (response.isSuccessful) {
                                                    navController.navigate("verify/$recoveryEmail")
                                                } else {
                                                    errorMessage = "Error HTTP ${response.code()}"
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

                        composable(
                            route = "password_new/{email}",
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
                                            val response = AuthRepository.resetPassword(
                                                NewPasswordRequest(email, recoveryPassword)
                                            )
                                            withContext(Dispatchers.Main) {
                                                isLoading = false
                                                if (response.success) {
                                                    navController.navigate("password_success")
                                                } else {
                                                    errorMessage = response.message
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

