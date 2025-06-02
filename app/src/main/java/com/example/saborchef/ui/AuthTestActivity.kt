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
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class AuthTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AuthTestScreen()
                }
            }
        }
    }
}

@Composable
fun AuthTestScreen(
    viewModel: LoginViewModel = viewModel()
) {
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
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = alias,
            onValueChange = { alias = it },
            label = { Text("Alias") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
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
            enabled = loginState !is LoginViewModel.LoginState.Loading
        ) {
            when (loginState) {
                is LoginViewModel.LoginState.Loading -> {
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
            is LoginViewModel.LoginState.Success -> {
                Text(
                    text = "¡Login exitoso! Token: ${currentState.token.take(20)}...",
                    color = MaterialTheme.colors.primary
                )
            }
            is LoginViewModel.LoginState.Error -> {
                Text(
                    text = "Error: ${currentState.message}",
                    color = MaterialTheme.colors.error
                )
            }
            is LoginViewModel.LoginState.Loading -> {
                Text("Cargando...")
            }
            else -> {
                Text("Ingresa tus credenciales")
            }
        }
    }
}