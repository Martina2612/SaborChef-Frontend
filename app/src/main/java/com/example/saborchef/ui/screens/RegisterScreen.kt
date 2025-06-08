// src/main/java/com/example/saborchef/ui/screens/RegisterScreen.kt
package com.example.saborchef.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
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
import com.example.saborchef.viewmodel.FieldState
import com.example.saborchef.viewmodel.RegisterUiState
import com.example.saborchef.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    onRegisterSuccess: (email: String) -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val aliasState by viewModel.aliasState.collectAsState()
    val emailState by viewModel.emailState.collectAsState()

    var userType by remember { mutableStateOf(Rol.USUARIO) }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // cuando uiState cambia
    LaunchedEffect(uiState) {
        when (uiState) {
            is RegisterUiState.Success -> {
                // auth.email es String? por eso el ?: ""
                val resultEmail = (uiState as RegisterUiState.Success).auth.email ?: ""
                onRegisterSuccess(resultEmail)
            }
            is RegisterUiState.Error -> {
                // demás errores ya los muestra aliasState/emailState o passwordError
                Log.e("RegisterScreen","Error en registro: ${(uiState as RegisterUiState.Error).message}")
            }
            else -> { /* Idle o Loading */ }
        }
    }

    BackHandler { navController.popBackStack() }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 56.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Text("Registro", fontFamily = Poppins, fontSize = 24.sp, color = BlueDark)
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(R.drawable.chef_register),
                contentDescription = null,
                modifier = Modifier.height(180.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Descubre más de 49.000 recetas",
                fontFamily = Poppins,
                fontSize = 16.sp,
                color = BlueLight
            )
            Spacer(Modifier.height(24.dp))

            UserTypeToggle(selected = userType.name) {
                userType = Rol.valueOf(it.uppercase())
            }
            Spacer(Modifier.height(16.dp))

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Apellido
            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Alias
            OutlinedTextField(
                value = alias,
                onValueChange = {
                    alias = it
                    viewModel.checkAlias(it)
                },
                label = { Text("Alias") },
                isError = aliasState is FieldState.Taken || aliasState is FieldState.Error,
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )
            when (aliasState) {
                is FieldState.Taken -> {
                    Text("Alias no disponible", color = Color.Red, fontSize = 12.sp)
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = 4.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column {
                            (aliasState as FieldState.Taken).suggestions.forEach { s ->
                                Text(
                                    s,
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable { alias = s; viewModel.checkAlias(s) }
                                        .padding(12.dp)
                                )
                            }
                        }
                    }
                }
                is FieldState.Error -> {
                    Text((aliasState as FieldState.Error).message, color = Color.Red, fontSize = 12.sp)
                }
                else -> {}
            }
            Spacer(Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.checkEmail(it)
                },
                label = { Text("Email") },
                isError = emailState is FieldState.Taken || emailState is FieldState.Error,
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            when (emailState) {
                is FieldState.Taken ->
                    Text("Email ya registrado", color = Color.Red, fontSize = 12.sp)
                is FieldState.Error ->
                    Text((emailState as FieldState.Error).message, color = Color.Red, fontSize = 12.sp)
                else -> {}
            }
            Spacer(Modifier.height(12.dp))

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Contraseña") },
                isError = passwordError != null,
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordError = null
                },
                label = { Text("Confirmar contraseña") },
                isError = passwordError != null,
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            passwordError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(Modifier.height(24.dp))

            // Botón Siguiente
            AppButton(
                text = if (uiState is RegisterUiState.Loading) "Registrando..." else "Siguiente",
                onClick = {
                    if (password != confirmPassword) {
                        passwordError = "Las contraseñas no coinciden"
                    } else {
                        viewModel.register(
                            nombre.trim(),
                            apellido.trim(),
                            alias.trim(),
                            email.trim(),
                            password,
                            userType
                        )
                    }
                },
                enabled = uiState !is RegisterUiState.Loading,
                primary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row {
                Text("¿Ya tenés cuenta? ")
                Text(
                    "Iniciá sesión",
                    color = Orange,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
            Spacer(Modifier.height(40.dp))
        }

        // Flecha “Up”
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .background(Color.White, CircleShape)
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás", tint = BlueDark)
        }
    }
}
