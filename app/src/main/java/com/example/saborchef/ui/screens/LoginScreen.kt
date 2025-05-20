package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.R
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLogin: (email: String, password: String, remember: Boolean) -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Flecha atrás
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(36.dp)
                .background(Color.White, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                tint = BlueDark
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Imagen circular
            Image(
                painter = painterResource(R.drawable.chef_login),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(24.dp))

            // Título
            Text(
                "¡Bienvenido!",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = BlueDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            // Campo Email con bordes redondeados
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email o alias", fontFamily = Poppins, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = BlueDark)
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = BlueDark,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = BlueDark,
                    backgroundColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(16.dp))

            // Campo Contraseña con bordes redondeados
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", fontFamily = Poppins, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BlueDark)
                },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = null, tint = BlueDark)
                    }
                },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = BlueDark,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = BlueDark,
                    backgroundColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(20.dp))

            // Olvidaste contraseña y debajo Recordarme
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "Olvidaste tu contraseña?",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = BlueDark,
                    modifier = Modifier.clickable(onClick = onForgotPassword)
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Orange,
                            uncheckedThumbColor = Color.LightGray,
                            checkedTrackColor = Orange.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Recordarme",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BlueDark
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Botón Iniciar sesión
            AppButton(
                text = "Iniciar sesión",
                onClick = { onLogin(email, password, rememberMe) },
                primary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Link Registrarse
            Row {
                Text(
                    "¿No tenés una cuenta? ",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = BlueDark
                )
                Text(
                    "Registrate",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Orange,
                    modifier = Modifier.clickable(onClick = onRegister)
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onBack = {},
        onLogin = { _, _, _ -> },
        onForgotPassword = {},
        onRegister = {}
    )
}
