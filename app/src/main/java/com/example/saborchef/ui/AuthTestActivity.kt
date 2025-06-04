package com.example.saborchef.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.saborchef.model.*
import com.example.saborchef.network.AuthRepository
import com.example.saborchef.network.NewPasswordRequest
import com.example.saborchef.network.PasswordResetRequest
import com.example.saborchef.network.VerifyCodeRequest
import com.example.saborchef.ui.screens.*
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.LoginState
import com.example.saborchef.viewmodel.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = AuthRoutes.Login.route
                ) {
                    // Paso 0: Login
                    composable(AuthRoutes.Login.route) {
                        val viewModel: LoginViewModel = viewModel()
                        var alias by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        val loginState by viewModel.loginState.collectAsState()
                        val scope = rememberCoroutineScope()

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Test de Login",
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            OutlinedTextField(
                                value = alias,
                                onValueChange = { alias = it },
                                label = { Text("Alias") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.login(alias, password)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = loginState !is LoginState.Loading
                            ) {
                                when (loginState) {
                                    is LoginState.Loading -> {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colors.onPrimary
                                        )
                                    }
                                    else -> {
                                        Text("Login")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            when (val currentState = loginState) {
                                is LoginState.Success -> {
                                    Text(
                                        text = "¡Login exitoso! Token: ${currentState.token.take(20)}...",
                                        color = MaterialTheme.colors.primary
                                    )
                                }
                                is LoginState.Error -> {
                                    Text(
                                        text = "Error: ${currentState.message}",
                                        color = MaterialTheme.colors.error
                                    )
                                }
                                is LoginState.Loading -> {
                                    Text("Cargando...")
                                }
                                is LoginState.Idle -> {
                                    Text("Ingresa tus credenciales")
                                }
                            }
                        }
                    }

                    // Paso 1: Ingresar email
                    composable(AuthRoutes.PasswordRecovery.route) {
                        var email by remember { mutableStateOf("") }
                        var isLoading by remember { mutableStateOf(false) }
                        var errorMessage by remember { mutableStateOf<String?>(null) }

                        PasswordEmailScreen(
                            email = email,
                            onEmailChange = { email = it },
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onSubmit = {
                                isLoading = true
                                errorMessage = null
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val response = AuthRepository.sendPasswordResetEmailRaw(PasswordResetRequest(email))
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            if (response.isSuccessful) {
                                                navController.navigate(AuthRoutes.PasswordCode.route + "?email=$email")
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

                    // Paso 2: Ingresar código
                    composable(AuthRoutes.PasswordCode.route + "?email={email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        var code by remember { mutableStateOf("") }
                        var isLoading by remember { mutableStateOf(false) }
                        var errorMessage by remember { mutableStateOf<String?>(null) }

                        PasswordCodeScreen(
                            email = email,
                            code = code,
                            onCodeChange = { code = it },
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onSubmit = {
                                isLoading = true
                                errorMessage = null
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val result = AuthRepository.verifyResetCode(VerifyCodeRequest(email, code))
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            if (result.success) {
                                                navController.navigate(AuthRoutes.PasswordNew.route + "?email=$email")
                                            } else {
                                                errorMessage = result.message
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

                    // Paso 3: Nueva contraseña
                    composable(AuthRoutes.PasswordNew.route + "?email={email}") { backStackEntry ->
                        val email = backStackEntry.arguments?.getString("email") ?: ""
                        var password by remember { mutableStateOf("") }
                        var isLoading by remember { mutableStateOf(false) }
                        var errorMessage by remember { mutableStateOf<String?>(null) }

                        PasswordNewScreen(
                            password = password,
                            onPasswordChange = { password = it },
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            onSubmit = {
                                isLoading = true
                                errorMessage = null
                                CoroutineScope(Dispatchers.IO).launch {
                                    val response = AuthRepository.resetPassword(NewPasswordRequest(email, password))
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                        if (response.success) {
                                            navController.navigate(AuthRoutes.PasswordSuccess.route)
                                        } else {
                                            errorMessage = response.message
                                        }
                                    }
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // Paso 4: Pantalla de éxito
                    composable(AuthRoutes.PasswordSuccess.route) {
                        PasswordSuccessScreen(
                            onBackToLogin = {
                                navController.popBackStack(AuthRoutes.Login.route, inclusive = false)
                            }
                        )
                    }
                }
            }
        }
    }
}
