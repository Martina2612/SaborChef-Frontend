package com.example.saborchef.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.saborchef.ui.screens.LoginScreen
import com.example.saborchef.ui.theme.SaborChefTheme

class AuthTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaborChefTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoginScreen(
                        onBack = {
                            // Acción para volver atrás
                            finish()
                        },
                        onLoginSuccess = { token ->
                            // Acción cuando el login es exitoso
                            println("Login exitoso! Token: $token")
                            // Aquí puedes navegar a otra pantalla
                        },
                        onForgotPassword = {
                            // Acción para recuperar contraseña
                            println("Recuperar contraseña")
                        },
                        onRegister = {
                            // Acción para registrarse
                            println("Ir a registro")
                        }
                    )
                }
            }
        }
    }
}