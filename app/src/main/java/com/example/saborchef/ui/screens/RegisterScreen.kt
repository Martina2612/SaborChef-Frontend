package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.saborchef.R
import com.example.saborchef.models.RegisterRequest
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.UserTypeSelector
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.viewmodel.RegisterUiState
import com.example.saborchef.viewmodel.RegisterViewModel
import kotlin.random.Random

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Local form state
    var userType by remember { mutableStateOf(RegisterRequest.Role.ALUMNO) }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Validation errors
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var aliasError by remember { mutableStateOf<String?>(null) }
    var suggestedAlias by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).size(36.dp).background(Color.White, shape = CircleShape)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = BlueDark)
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Text("Registro", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, color = BlueDark)
            Spacer(Modifier.height(8.dp))
            Image(painter = painterResource(R.drawable.chef_register), contentDescription = null, modifier = Modifier.height(180.dp), contentScale = ContentScale.Fit)
            Spacer(Modifier.height(16.dp))
            Text("Descubre más de 4000 recetas", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = BlueLight)
            Spacer(Modifier.height(24.dp))

            UserTypeSelector(selected = userType.name, onSelect = { userType = RegisterRequest.Role.valueOf(it.uppercase()) })
            Spacer(Modifier.height(16.dp))

            // Alias field
            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it; aliasError = null; suggestedAlias = null },
                label = { Text("Alias") }, modifier = Modifier.fillMaxWidth().height(56.dp),
                singleLine = true, leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
            aliasError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
            suggestedAlias?.let {
                Text("Sugerencia: $it", color = BlueDark, modifier = Modifier.clickable { alias = it; suggestedAlias = null })
            }
            Spacer(Modifier.height(12.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Email") }, singleLine = true,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            emailError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
            Spacer(Modifier.height(12.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Contraseña") }, singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null) } },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; passwordError = null },
                label = { Text("Confirmar contraseña") }, singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null) } },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
            passwordError?.let { Text(it, color = Color.Red, fontSize = 12.sp) }
            Spacer(Modifier.height(24.dp))

            // Submit
            AppButton(
                text = "Siguiente",
                onClick = {
                    // Validaciones frontend
                    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
                    when {
                        !email.matches(emailRegex.toRegex()) -> emailError = "Formato de email inválido"
                        password != confirmPassword -> passwordError = "Las contraseñas no coinciden"
                        password.length < 6 || !password.any { it.isDigit() } || !password.any { it.isLetter() } ->
                            passwordError = "Contraseña debe ser alfanumérica y mínimo 6 caracteres"
                        else -> {
                            viewModel.register(nombre, apellido, alias, email, password, userType)
                        }
                    }
                },
                primary = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            Row { Text("¿Ya tenés cuenta? ")
                Text("Iniciá sesión", modifier = Modifier.clickable { onLoginClick() }, color = Orange)
            }

            // Observe backend errors
            if (uiState is RegisterUiState.Error) {
                val msg = (uiState as RegisterUiState.Error).message
                if (msg.contains("alias")) {
                    aliasError = msg
                    suggestedAlias = alias + Random.nextInt(10,99)
                } else if (msg.contains("email")) {
                    emailError = msg
                } else {
                    // generic error
                    Text(msg, color = Color.Red)
                }
            }
            if (uiState is RegisterUiState.Success) {
                LaunchedEffect(Unit) { onRegisterSuccess() }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onBack = {}, onLoginClick = {}, onRegisterSuccess = {})
}
