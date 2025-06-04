package com.example.saborchef.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    onLoginSuccess: (token: String) -> Unit,
    onForgotPassword: () -> Unit,
    onRegister: () -> Unit,
) {
    var alias by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
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

            Image(
                painter = painterResource(R.drawable.chef_login),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "¡Bienvenido!",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = BlueDark
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = { Text("Alias", fontFamily = Poppins, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = BlueDark)
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

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", fontFamily = Poppins, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BlueDark)
                },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
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

            AppButton(
                text = "Iniciar sesión",
                onClick = {
                    if (alias.isNotBlank() && password.isNotBlank()) {
                        // Acá deberías invocar el login real si se va a usar
                        Toast.makeText(context, "Login simulado: $alias", Toast.LENGTH_SHORT).show()
                        onLoginSuccess("fake-token-$alias")
                    } else {
                        Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                primary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

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
