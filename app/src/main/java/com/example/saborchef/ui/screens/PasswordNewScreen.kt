package com.example.saborchef.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins

@Composable
fun PasswordNewScreen(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    // Validaciones en tiempo real
    val lengthValid = password.length >= 8
    val numberValid = password.any { it.isDigit() }
    val passwordsMatch = password == confirmPassword

    // Mensaje de error para confirmación
    val confirmError = if (confirmPassword.isNotEmpty() && !passwordsMatch)
        "Las contraseñas no coinciden" else null

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top bar manual
        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atrás",
                    tint = BlueDark
                )
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea tu nueva contraseña",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = BlueDark
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Ingresa tu nueva contraseña",
                fontFamily = Poppins,
                fontSize = 14.sp,
                color = BlueDark.copy(alpha = 0.7f)
            )
            Spacer(Modifier.height(24.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña nueva", fontFamily = Poppins, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BlueDark) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = BlueDark
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = errorMessage != null,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            // Requisitos
            Spacer(Modifier.height(16.dp))
            Column(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (lengthValid) Orange else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Al menos 8 caracteres",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = if (numberValid) Orange else Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Al menos un número",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = { Text("Repetir contraseña", fontFamily = Poppins, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BlueDark) },
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            imageVector = if (confirmVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = BlueDark
                        )
                    }
                },
                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                isError = confirmError != null,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
            confirmError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // Botón “Listo!”
            AppButton(
                text = if (isLoading) "Guardando..." else "Listo!",
                onClick = {
                    if (!isLoading) {
                        if (lengthValid && numberValid && passwordsMatch) {
                            onSubmit()
                        } else {
                            Toast.makeText(context, "Cumplí todos los requisitos", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                primary = true,
                enabled = lengthValid && numberValid && passwordsMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordNewScreenPreview() {
    PasswordNewScreen(
        password = "",
        onPasswordChange = {},
        confirmPassword = "",
        onConfirmPasswordChange = {},
        isLoading = false,
        errorMessage = null,
        onSubmit = {},
        onBack = {}
    )
}
