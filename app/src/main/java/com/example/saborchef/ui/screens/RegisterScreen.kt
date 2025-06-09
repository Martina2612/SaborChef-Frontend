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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.example.saborchef.ui.theme.*
import com.example.saborchef.viewmodel.*

@Composable
fun RegisterScreen(
    navController: NavController,
    sharedAlumnoViewModel: SharedAlumnoViewModel,
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

    var aliasChecked by remember { mutableStateOf("") }
    var emailChecked by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(alias) {
        if (alias != aliasChecked) {
            viewModel.checkAlias(alias)
            aliasChecked = alias
        }
    }

    LaunchedEffect(email) {
        if (email != emailChecked) {
            viewModel.checkEmail(email)
            emailChecked = email
        }
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is RegisterUiState.Success -> {
                val resultEmail = state.auth.email ?: ""
                if (state.auth.role == "ALUMNO") {
                    navController.navigate("upload_dni")
                } else {
                    onRegisterSuccess(resultEmail)
                }
            }
            is RegisterUiState.Error -> {
                Log.e("RegisterScreen", "Error en registro: ${state.message}")
            }
            else -> {}
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
            Text("Registro", fontFamily = Poppins, fontSize = 24.sp, color = BlueDark, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Image(
                painter = painterResource(R.drawable.chef_register),
                contentDescription = null,
                modifier = Modifier.height(180.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(12.dp))
            Text("Descubre más de 49.000 recetas", fontFamily = Poppins, fontSize = 16.sp, color = BlueLight)
            Spacer(Modifier.height(24.dp))

            UserTypeToggle(selected = userType.name) {
                userType = Rol.valueOf(it.uppercase())
            }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = {
                    Text(
                        "Nombre",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BlueDark)
                },
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

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = {
                    Text(
                        "Apellido",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BlueDark)
                },
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

            OutlinedTextField(
                value = alias,
                onValueChange = { alias = it },
                label = {
                    Text(
                        "Alias",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                },
                isError = aliasState is FieldState.Taken || aliasState is FieldState.Error,
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = BlueDark)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = if (aliasState is FieldState.Taken || aliasState is FieldState.Error) Color.Red else BlueDark,
                    unfocusedIndicatorColor = if (aliasState is FieldState.Taken || aliasState is FieldState.Error) Color.Red else Color.LightGray,
                    cursorColor = BlueDark,
                    backgroundColor = Color.Transparent
                )
            )

            when (aliasState) {
                is FieldState.Taken -> {
                    Text("Alias no disponible", color = Color.Red, fontSize = 12.sp)
                    Card(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = 4.dp,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column {
                            (aliasState as FieldState.Taken).suggestions.forEach { suggestion ->
                                Text(
                                    suggestion,
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable { alias = suggestion }
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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        "Email",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                },
                isError = emailState is FieldState.Taken || emailState is FieldState.Error,
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = BlueDark)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = if (emailState is FieldState.Taken || emailState is FieldState.Error) Color.Red else BlueDark,
                    unfocusedIndicatorColor = if (emailState is FieldState.Taken || emailState is FieldState.Error) Color.Red else Color.LightGray,
                    cursorColor = BlueDark,
                    backgroundColor = Color.Transparent
                )
            )
            when (emailState) {
                is FieldState.Taken -> Text("Email ya registrado", color = Color.Red, fontSize = 12.sp)
                is FieldState.Error -> Text((emailState as FieldState.Error).message, color = Color.Red, fontSize = 12.sp)
                else -> {}
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = {
                    Text(
                        "Contraseña",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                },
                isError = passwordError != null,
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BlueDark)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = BlueDark
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = if (passwordError != null) Color.Red else BlueDark,
                    unfocusedIndicatorColor = if (passwordError != null) Color.Red else Color.LightGray,
                    cursorColor = BlueDark,
                    backgroundColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordError = null
                },
                label = {
                    Text(
                        "Confirmar contraseña",
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = BlueDark
                    )
                },
                isError = passwordError != null,
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BlueDark)
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = BlueDark
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                    focusedIndicatorColor = if (passwordError != null) Color.Red else BlueDark,
                    unfocusedIndicatorColor = if (passwordError != null) Color.Red else Color.LightGray,
                    cursorColor = BlueDark,
                    backgroundColor = Color.Transparent
                )
            )
            Spacer(Modifier.height(24.dp))

            passwordError?.let {
                Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(Modifier.height(24.dp))

            AppButton(
                text = "Siguiente",
                onClick = {
                    when {
                        password != confirmPassword -> {
                            passwordError = "Las contraseñas no coinciden"
                        }
                        aliasState !is FieldState.Valid -> {
                            passwordError = "Alias inválido o no verificado"
                        }
                        emailState !is FieldState.Valid -> {
                            passwordError = "Email inválido o no verificado"
                        }
                        else -> {
                            sharedAlumnoViewModel.setUserInfo(
                                nombre.trim(),
                                apellido.trim(),
                                alias.trim(),
                                email.trim(),
                                password,
                                userType
                            )

                            if (userType == Rol.ALUMNO) {
                                navController.navigate("upload_dni")
                            } else {
                                viewModel.register(context, sharedAlumnoViewModel)
                            }
                        }
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
