// RegisterScreen.kt
package com.example.saborchef.ui.screens

import androidx.activity.compose.BackHandler
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.R
import com.example.saborchef.model.Rol
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.UserTypeToggle
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.RegisterUiState
import com.example.saborchef.viewmodel.RegisterViewModel
import kotlin.random.Random

@Composable
fun RegisterScreen(
    navController: NavController,
    onRegisterSuccess: (email: String) -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    var userType by remember { mutableStateOf(Rol.USUARIO) }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var aliasError by remember { mutableStateOf<String?>(null) }
    var suggestedAlias by remember { mutableStateOf<String?>(null) }

    // Maneja también botón de sistema atrás
    BackHandler {
        navController.popBackStack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()

        // 1) Contenido principal con scroll
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp),  // deja espacio para la flecha
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Text("Registro", fontFamily = Poppins, fontSize = 24.sp, color = BlueDark)
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(R.drawable.chef_register),
                contentDescription = null,
                modifier = Modifier.height(180.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Descubre más de 4000 recetas",
                fontFamily = Poppins,
                fontSize = 16.sp,
                color = BlueLight
            )
            Spacer(Modifier.height(24.dp))

            UserTypeToggle(selected = userType.name, onSelect = {
                userType = Rol.valueOf(it.uppercase())
            })

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = apellido, onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = alias,
                onValueChange = {
                    alias = it; aliasError = null; suggestedAlias = null
                },
                label = { Text("Alias") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
            aliasError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
            suggestedAlias?.let { sugerencia ->
                Text(
                    "Sugerencia: $sugerencia",
                    color = BlueDark,
                    modifier = Modifier.clickable {
                        alias = sugerencia; suggestedAlias = null
                    }
                )
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it; emailError = null
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            emailError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it; passwordError = null
                },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it; passwordError = null
                },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = {
                        confirmPasswordVisible = !confirmPasswordVisible
                    }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible)
                    VisualTransformation.None
                else PasswordVisualTransformation()
            )
            passwordError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
            Spacer(Modifier.height(24.dp))

            AppButton(
                text = "Siguiente",
                onClick = {
                    val emailRegex = """^[A-Za-z](.*)([@]{1})(.{1,})(\.)(.{1,})""".toRegex()
                    when {
                        !email.matches(emailRegex) -> {
                            emailError = "Formato de email inválido"
                            Log.d("RegistroDebug", "Email inválido: $email")
                        }
                        password != confirmPassword -> {
                            passwordError = "Las contraseñas no coinciden"
                            Log.d("RegistroDebug", "Contraseñas no coinciden")
                        }
                        password.length < 6
                                || !password.any { it.isDigit() }
                                || !password.any { it.isLetter() } -> {
                            passwordError =
                                "Contraseña debe ser alfanumérica y mínimo 6 caracteres"
                            Log.d("RegistroDebug", "Contraseña inválida: $password")
                        }
                        else -> {
                            Log.d("RegistroDebug", "→ Registrando...")
                            viewModel.register(
                                nombre, apellido, alias,
                                email, password, userType
                            )
                        }
                    }
                },
                primary = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            Row {
                Text("¿Ya tenés cuenta? ")
                Text(
                    text = "Iniciá sesión",
                    color = Orange,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }

            when (uiState) {
                is RegisterUiState.Error -> {
                    val msg = (uiState as RegisterUiState.Error).message
                    if (msg.contains("alias", ignoreCase = true)) {
                        aliasError = msg
                        suggestedAlias = alias + Random.nextInt(10, 99)
                    } else if (msg.contains("email", ignoreCase = true)) {
                        emailError = msg
                    } else {
                        Text(
                            text = msg,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                is RegisterUiState.Success -> {
                    LaunchedEffect(Unit) {
                        onRegisterSuccess(
                            (uiState as RegisterUiState.Success)
                                .auth.email ?: ""
                        )
                    }
                }
                else -> {}
            }

            Spacer(Modifier.height(40.dp))
        }

        // 2) Botón de retroceso siempre encima
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .background(Color.White, shape = CircleShape)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver atrás",
                tint = BlueDark,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
