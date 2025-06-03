// app/src/main/java/com/example/saborchef/ui/AuthTestActivity.kt

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
import com.example.saborchef.viewmodel.LoginState       // <-- Importamos la clase sellada de primer nivel
import com.example.saborchef.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

/**
 * Esta Activity permite probar el flujo de login contra el backend.
 * Muestra dos campos (alias + password) y un botón “Login”.
 * Luego despliega el estado (Idle, Loading, Success(token) o Error(mensaje)).
 */
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

    // Ahora sí recogemos loginState como StateFlow<LoginState>
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
            // Aquí ya no referenciamos LoginViewModel.LoginState.Loading,
            // sino LoginState.Loading (la clase sellada de nivel superior).
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

        // Mostramos un texto según el estado actual
        when (val currentState = loginState) {
            is LoginState.Success -> {
                // currentState.token es el String que vino del servidor
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
