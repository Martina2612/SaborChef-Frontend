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
import androidx.compose.material.icons.filled.Person
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
import com.example.saborchef.ui.components.UserTypeSelector
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterClick: (String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit
) {
    var userType by remember { mutableStateOf("Alumno") }
    var alias by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
            Spacer(Modifier.height(40.dp))

            Text(
                "Registro",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = BlueDark
            )

            Spacer(Modifier.height(8.dp))

            Image(
                painter = painterResource(R.drawable.chef_register), // Reemplazá por tu recurso
                contentDescription = null,
                modifier = Modifier.height(180.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Descubre más de 4000 recetas",
                fontFamily = Poppins,
                fontWeight= FontWeight.SemiBold,
                fontSize = 16.sp,
                color = BlueLight
            )

            Spacer(Modifier.height(24.dp))

            UserTypeSelector(
                selected = userType,
                onSelect = { userType = it }
            )

            Spacer(Modifier.height(16.dp))

            // Alias
            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = { Text("Alias", fontFamily = Poppins, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BlueDark
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = BlueDark,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = BlueDark,
                    backgroundColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontFamily = Poppins, fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = BlueDark)
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = BlueDark,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = BlueDark,
                    backgroundColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(12.dp))

            // Contraseña
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

            Spacer(Modifier.height(12.dp))

            // Confirmar contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirme contraseña", fontFamily = Poppins, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BlueDark)
                },
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(icon, contentDescription = null, tint = BlueDark)
                    }
                },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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

            Spacer(Modifier.height(24.dp))

            AppButton(
                text = "Siguiente",
                onClick = {
                    onRegisterClick(userType, alias, email, password, confirmPassword)
                },
                primary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Row {
                Text(
                    "Ya tenes una cuenta? ",
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = BlueDark
                )
                Text(
                    "Iniciá sesión",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Orange,
                    modifier = Modifier.clickable(onClick = onLoginClick)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onBack = {},
        onRegisterClick = { _, _, _, _, _ -> },
        onLoginClick = {}
    )
}