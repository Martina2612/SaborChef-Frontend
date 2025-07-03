package com.example.saborchef

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.network.AuthRepository
import com.example.saborchef.network.NewPasswordRequest
import com.example.saborchef.network.PasswordResetRequest
import com.example.saborchef.ui.screens.*
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.saborchef.model.CursoInscripto
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.theme.Orange
import com.google.gson.Gson



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface {
                    val context = LocalContext.current
                    val dataStoreManager = remember { DataStoreManager(context) }
                    val navController = rememberNavController()
                    val searchViewModel: SearchViewModel = viewModel()
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = LoginViewModelFactory(dataStoreManager)
                    )
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
                    val sharedCursoViewModel: SharedCursoViewModel = viewModel()


                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("welcome") {
                            WelcomeScreen(
                                navController = navController,
                                onContinueAsUser = { navController.navigate("auth") },
                                onContinueAsGuest = {
                                    navController.navigate("home")
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
                                    // Los datos del DNI ya se guardaron en el ViewModel dentro de DniUploadScreen
                                    // Ahora navegamos a la verificación del email
                                    navController.navigate("verify_registration/${sharedAlumnoViewModel.email}") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                sharedAlumnoViewModel = sharedAlumnoViewModel,
                                registerViewModel = registerViewModel
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
                            SimpleHomeScreen(nombre = alias.ifBlank { null },
                                navController = navController )
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

                        composable("cursos") {
                            CursosScreen(navController = navController)
                        }

                        composable("curso_detalle/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val cursoId = backStackEntry.arguments?.getLong("id") ?: 0
                            val cursoViewModel: CursoViewModel = viewModel()

                            var rol by remember { mutableStateOf<Rol?>(null) }

                            LaunchedEffect(Unit) {
                                dataStoreManager.role.collect {
                                    rol = it?.let { valor -> Rol.valueOf(valor) }
                                }
                            }

                            val curso by cursoViewModel.cursoDetalle.collectAsState()

                            if (rol != null) {
                                CursoDetalleScreen(
                                    cursoId = cursoId,
                                    navController = navController,
                                    cursoViewModel = cursoViewModel,
                                    userRole = rol!!,
                                    sharedCursoViewModel = sharedCursoViewModel
                                )
                            } else {
                                // Mostrar loading mientras carga el rol
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = OrangeDark)
                                }
                            }
                        }

                        composable("sedes_disponibles") {

                            val cronogramas = sharedCursoViewModel.cronogramas

                            SedesDisponiblesScreen(
                                cronogramas = cronogramas,
                                onVolver = { navController.popBackStack() },
                                navController = navController
                            )
                        }

                        composable(
                            "sucursal_detalle/{sedeId}/{cronogramaId}/{mostrarBotonConfirmar}",
                            arguments = listOf(
                                navArgument("sedeId") { type = NavType.LongType },
                                navArgument("cronogramaId") { type = NavType.LongType },
                                navArgument("mostrarBotonConfirmar") { type = NavType.BoolType }
                            )
                        ) { backStackEntry ->
                            val sedeId = backStackEntry.arguments?.getLong("sedeId") ?: 0L
                            val cronogramaId = backStackEntry.arguments?.getLong("cronogramaId") ?: 0L
                            val mostrarBotonConfirmar = backStackEntry.arguments?.getBoolean("mostrarBotonConfirmar") ?: true

                            SucursalDetalleScreen(
                                sedeId = sedeId,
                                cronogramaId = cronogramaId,
                                navController = navController,
                                rol = Rol.ALUMNO,
                                mostrarBotonConfirmar = mostrarBotonConfirmar
                            )
                        }


                        composable("mis_cursos") {
                            MisCursosScreen(navController)
                        }

                        composable("mis_cursos_detalle/{cursoJson}") { backStackEntry ->
                            val json = backStackEntry.arguments?.getString("cursoJson") ?: ""
                            val curso = Gson().fromJson(json, CursoInscripto::class.java)
                            MisCursosDetalleScreen(curso, navController)
                        }


















                    }
                }
            }
        }
    }
}
