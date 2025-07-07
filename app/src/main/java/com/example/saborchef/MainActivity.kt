package com.example.saborchef

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.example.saborchef.data.SessionManager

import androidx.compose.ui.platform.LocalContext

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.saborchef.data.DataStoreManager

import com.example.saborchef.model.Rol
import com.example.saborchef.ui.screens.MisDatosScreen
import com.example.saborchef.network.AuthRepository
import com.example.saborchef.network.NewPasswordRequest
import com.example.saborchef.network.PasswordResetRequest
import com.example.saborchef.ui.publish.PublishRecipeScreen
import com.example.saborchef.ui.screens.*
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.model.CursoInscripto
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.screens.EditRecipeScreen
import com.example.saborchef.ui.theme.*
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedGetBackStackEntry")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface {
                    val context = LocalContext.current
                    val dataStoreManager = remember { DataStoreManager(context) }
                    val navController = rememberNavController()
                    val searchViewModel: SearchViewModel = viewModel()

                    // LoginViewModel con factory corregido - solo Application
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                @Suppress("UNCHECKED_CAST")
                                return LoginViewModel(application) as T
                            }
                        }
                    )

                    val registerViewModel: RegisterViewModel = viewModel()
                    val sharedAlumnoViewModel: SharedAlumnoViewModel = viewModel()
                    val scope = rememberCoroutineScope()

                    var alias by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var recoveryEmail by remember { mutableStateOf("") }
                    var recoveryPassword by remember { mutableStateOf("") }
                    var confirmPassword by remember { mutableStateOf("") }
                    var isLoading by remember { mutableStateOf(false) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    var resetTimerTrigger by remember { mutableIntStateOf(0) }
                    val loginState by loginViewModel.loginState.collectAsState()
                    val sharedCursoViewModel: SharedCursoViewModel = viewModel()

                    val dataStore = remember { DataStoreManager(this@MainActivity) }
                    LaunchedEffect(Unit) {
                        val storedRole = dataStore.role.firstOrNull()
                        if (storedRole.isNullOrBlank()) {
                            dataStore.saveRole(Rol.VISITANTE.name)
                        }
                    }
                    val roleString by dataStore.role.collectAsState(initial = "")
                    val userRole = remember(roleString) {
                        runCatching { Rol.valueOf(roleString ?: Rol.VISITANTE.name) }.getOrElse { Rol.VISITANTE }
                    }

                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("welcome") {
                            LaunchedEffect(Unit) {
                                dataStore.clearUserData()
                                alias = ""
                                password = ""
                            }
                            WelcomeScreen(
                                navController = navController,
                                onContinueAsUser = { navController.navigate("auth") {
                                    popUpTo("welcome") { inclusive = true }
                                    launchSingleTop = true} },
                                onContinueAsGuest = {
                                    scope.launch {
                                        dataStore.saveRole(Rol.VISITANTE.name)
                                    }
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
                            var aliasLocal by remember { mutableStateOf("") }
                            var passwordLocal by remember { mutableStateOf("") }

                            // CORRECCIÓN: Usar el factory con application para AndroidViewModel
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = object : ViewModelProvider.Factory {
                                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                        @Suppress("UNCHECKED_CAST")
                                        return LoginViewModel(application) as T
                                    }
                                }
                            )

                            LoginScreen(
                                aliasValue = aliasLocal,
                                passwordValue = passwordLocal,
                                loginState = loginViewModel.loginState.collectAsState().value,
                                onAliasChange = { aliasLocal = it },
                                onPasswordChange = { passwordLocal = it },
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
                            RegisterScreen(navController, sharedAlumnoViewModel)
                        }

                        composable("upload_dni") {
                            DniUploadScreen(
                                onBack = { navController.popBackStack() },
                                onFinish = { frontUri: Uri?, backUri: Uri?, tramite: String ->
                                    // Guardamos la info del DNI en el ViewModel
                                    sharedAlumnoViewModel.setDniInfo(frontUri, backUri, tramite)

                                    // Navegamos a verificación del email después de subir DNI
                                    navController.navigate("verify_registration/${sharedAlumnoViewModel.email}/${sharedAlumnoViewModel.rol}") {
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

                        composable("add_payment_conversion") {
                            AddPaymentScreen(
                                sharedAlumnoViewModel = sharedAlumnoViewModel,
                                navController = navController,
                                viewModel = registerViewModel,
                                isConversion = true
                            )
                        }

                        composable("upload_dni_conversion") {
                            val dataStore = remember { DataStoreManager(this@MainActivity) }

                            DniUploadScreen(
                                onBack = { navController.popBackStack() },
                                onFinish = { frontUri: Uri?, backUri: Uri?, tramite: String ->
                                    // Para conversión, ir directo a verificar resultado
                                    navController.navigate("conversion_result") {
                                        popUpTo("add_payment_conversion") { inclusive = true }
                                    }
                                },
                                sharedAlumnoViewModel = sharedAlumnoViewModel,
                                registerViewModel = registerViewModel,
                                isConversion = true,
                                dataStoreManager = dataStore
                            )
                        }

                        composable("conversion_result") {
                            val registerState by registerViewModel.uiState.collectAsState()
                            val dataStore = remember { DataStoreManager(this@MainActivity) }

                            // Crear variable local para evitar smart cast issues
                            val currentState = registerState

                            when (currentState) {
                                is RegisterUiState.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(color = OrangeDark)
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text("Procesando conversión...", fontFamily = Poppins, color = BlueDark)
                                        }
                                    }
                                }
                                is RegisterUiState.SuccessUnit -> {
                                    // Conversión exitosa
                                    SuccessfulConversionScreen {
                                        scope.launch {
                                            // Actualizar rol en DataStore
                                            dataStore.updateUserRole(Rol.ALUMNO)
                                            // Resetear el ViewModel para futuras conversiones
                                            sharedAlumnoViewModel.reset()
                                            // Volver al perfil
                                            navController.navigate("profile") {
                                                popUpTo("conversion_result") { inclusive = true }
                                            }
                                        }
                                    }
                                }
                                is RegisterUiState.Error -> {
                                    ErrorConversionScreen(
                                        error = currentState.message,
                                        onRetry = {
                                            navController.navigate("add_payment_conversion") {
                                                popUpTo("conversion_result") { inclusive = true }
                                            }
                                        },
                                        onBack = {
                                            navController.navigate("profile") {
                                                popUpTo("conversion_result") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                else -> {
                                    // Estado inicial - redirigir al inicio del flujo
                                    LaunchedEffect(Unit) {
                                        navController.navigate("add_payment_conversion") {
                                            popUpTo("conversion_result") { inclusive = true }
                                        }
                                    }
                                }
                            }
                        }

                        composable(
                            "verify_registration/{email}/{role}",
                            arguments = listOf(
                                navArgument("email") { type = NavType.StringType },
                                navArgument("role") { type = NavType.StringType }
                            )
                        ) { backStack ->
                            val email = backStack.arguments!!.getString("email")!!
                            val roleParam = backStack.arguments!!.getString("role")!!

                            VerificationCodeScreen(
                                email = email,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    // CAMBIO: Siempre ir a successful_register cuando se verifica exitosamente
                                    navController.navigate("successful_register") {
                                        popUpTo("verify_registration/{email}/{role}") { inclusive = true }
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
                                    navController.navigate("login") {
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
                            val emailParam = backStackEntry.arguments?.getString("email") ?: ""
                            VerificationCodeScreen(
                                email = emailParam,
                                onBack = { navController.popBackStack() },
                                onNext = {
                                    navController.navigate("password_new/$emailParam")
                                },
                                onResendCode = {
                                    resetTimerTrigger++
                                    scope.launch(Dispatchers.IO) {
                                        AuthRepository.sendPasswordResetEmailRaw(
                                            PasswordResetRequest(emailParam)
                                        )
                                    }
                                },
                                resetTrigger = resetTimerTrigger
                            )
                        }
                        composable(
                            "password_new/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val emailParam = backStackEntry.arguments?.getString("email") ?: ""
                            PasswordNewScreen(
                                password = recoveryPassword,
                                onPasswordChange = { recoveryPassword = it },
                                confirmPassword = confirmPassword,
                                onConfirmPasswordChange = { confirmPassword = it },
                                isLoading = isLoading,
                                errorMessage = errorMessage,
                                onSubmit = {
                                    // en Submit ya tengo password y confirmPassword iguales
                                    isLoading = true
                                    errorMessage = null
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val resp = AuthRepository.resetPassword(
                                                NewPasswordRequest(emailParam, recoveryPassword)
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
                            PasswordUpdatedScreen(
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
                            val dataStore = remember { DataStoreManager(this@MainActivity) }
                            SimpleHomeScreen(
                                nombre = alias.ifBlank { null },
                                navController = navController,
                                role = userRole,
                                dataStoreManager = dataStore
                            )
                        }
                        composable("search") { backStackEntry ->
                            // Este será el owner para todo el flow de Search → Filter
                            val parentEntry = remember {
                                navController.getBackStackEntry("search")
                            }
                            // Aquí obtienes el VM y lo asocias a ese owner
                            val vm: SearchViewModel = viewModel(parentEntry)
                            SearchScreen(navController, vm, role = userRole)
                        }
                        composable("filter") { backStackEntry ->
                            // Reusa el mismo owner "search"
                            val parentEntry = remember {
                                navController.getBackStackEntry("search")
                            }
                            val vm: SearchViewModel = viewModel(parentEntry)
                            FilterScreen(navController, vm)
                        }
                        composable("recipe/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id") ?: "0"
                            RecipeDetailScreen(
                                recipeId = id,
                                navController = navController,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("favs") {
                            FavoriteRecipesScreen(
                                navController = navController,
                                role = userRole
                            )
                        }


                        composable("profile") {
                            val dataStore = remember { DataStoreManager(this@MainActivity) }

                            // Lee los datos del usuario desde DataStore
                            val userName by dataStore.alias.collectAsState(initial = "")
                            val storedRole by dataStore.role.collectAsState(initial = Rol.VISITANTE.name)

                            // Convierte el string a enum Rol
                            val currentUserRole = try {
                                Rol.valueOf(storedRole ?: Rol.VISITANTE.name)
                            } catch (e: IllegalArgumentException) {
                                Rol.VISITANTE
                            }

                            // URI de foto (por ahora null)
                            val photoUri: Uri? = null

                            ProfileScreen(
                                userName = alias.ifBlank { userName ?: "" },
                                photoUri = photoUri,
                                role = currentUserRole, // Ahora es de tipo Rol y funciona correctamente
                                onBack = { navController.popBackStack() },
                                onEditPhoto = { /* Implementar después */ },
                                onOptionClick = { label ->
                                    when(label) {
                                        "Mis datos" -> navController.navigate("my_data")
                                        "Mis recetas" -> navController.navigate("my_recipes")
                                        "Recetas editadas"-> navController.navigate("saved_recipes")
                                        "Mis cursos" -> navController.navigate("my_courses")
                                        "Medios de pago" -> navController.navigate("payment_methods")
                                        "Términos y condiciones" -> navController.navigate("terms")
                                        "Contáctanos" -> navController.navigate("contact")
                                    }
                                },
                                onBecomeStudent = {
                                    // Limpiar datos previos y empezar conversión
                                    sharedAlumnoViewModel.reset()
                                    navController.navigate("add_payment_conversion")
                                },
                                onLogout = { /* Se maneja dentro del ProfileScreen */ },
                                dataStoreManager = dataStore,
                                navController = navController
                            )
                        }

                        // NUEVA RUTA: MIS DATOS
                        composable("my_data") {
                            val dataStore = remember { DataStoreManager(this@MainActivity) }
                            val userId by dataStore.userId.collectAsState(initial = null)

                            // Usar el userId directamente o 1L como fallback
                            val safeUserId = userId ?: 1L

                            MisDatosScreen(
                                navController = navController,
                                userId = safeUserId
                            )
                        }

                        composable("publishRecipe") {
                            PublishRecipeScreen(
                                navController = navController
                            )
                        }
                        composable("terms") {
                            TermsConditionsScreen(
                                navController = navController,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("contact") {
                            ContactUsScreen(
                                navController = navController,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        // Agregar nueva ruta para escaladas
                        composable("saved_recipes") {
                            SavedRecipesScreen(navController = navController, onBack = { navController.popBackStack() })
                        }
                        composable("my_recipes") {
                            MyRecipesScreen(
                                navController = navController,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("edit_recipe") {
                            val receta = navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<RecetaDetalleResponse>("recetaSeleccionada")

                            if (receta != null) {
                                EditRecipeScreen(navController = navController, receta = receta)
                            } else {
                                // En caso de que algo falle, podrías volver atrás:
                                LaunchedEffect(Unit) { navController.popBackStack() }
                            }
                        }



                        composable("cursos") {
                            CursosScreen(navController = navController)
                        }

                        composable("curso_detalle/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val cursoId = backStackEntry.arguments?.getLong("id") ?: 0
                            val cursoViewModel: CursoViewModel = viewModel()

                            var userRole by remember { mutableStateOf<Rol?>(null) }

                            LaunchedEffect(Unit) {
                                dataStoreManager.role.collect { roleValue ->
                                    userRole = roleValue?.let { Rol.valueOf(it) }
                                }
                            }

                            if (userRole != null) {
                                CursoDetalleScreen(
                                    cursoId = cursoId,
                                    navController = navController,
                                    cursoViewModel = cursoViewModel,
                                    userRole = userRole!!,
                                    sharedCursoViewModel = sharedCursoViewModel
                                )
                            } else {
                                // Loading mientras carga el rol
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
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

                        composable("upgrade_to_student") {

                            val dataStore = remember { DataStoreManager(this@MainActivity) }
                            val currentRole by dataStore.role.collectAsState(initial = "")
                            val scope = rememberCoroutineScope()

                            MostrarPantallaSerAlumno(
                                onQuieroSerAlumno = {

                                    if (currentRole == Rol.VISITANTE.name) {
                                        navController.navigate("auth")
                                    } else {
                                        navController.navigate("register")

                                    }
                                },
                                onContinuar = {

                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SuccessfulConversionScreen(
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de éxito
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.Green,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Felicitaciones!",
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = BlueDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ya eres alumno de SaborChef.\nAhora puedes inscribirte a cursos y acceder a contenido exclusivo.",
            fontFamily = Poppins,
            fontSize = 16.sp,
            color = BlueDark.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppButton(
            text = "Continuar",
            onClick = onContinue,
            primary = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ErrorConversionScreen(
    error: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de error
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Error en la conversión",
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = BlueDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = error,
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = Color.Red,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppButton(
                text = "Volver",
                onClick = onBack,
                primary = false,
                modifier = Modifier.weight(1f)
            )

            AppButton(
                text = "Reintentar",
                onClick = onRetry,
                primary = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}