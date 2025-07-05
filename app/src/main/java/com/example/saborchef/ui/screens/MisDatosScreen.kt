package com.example.saborchef.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.saborchef.model.PerfilUsuarioDTO
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.theme.*
import com.example.saborchef.viewmodel.ActualizarPerfilState
import com.example.saborchef.viewmodel.MisDatosState
import com.example.saborchef.viewmodel.MisDatosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisDatosScreen(
    navController: NavController,
    userId: Long,
    viewModel: MisDatosViewModel = viewModel()
) {
    val datosState by viewModel.datosState.collectAsState()
    val actualizarState by viewModel.actualizarState.collectAsState()

    // Estados locales para los campos editables
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Cargar datos al iniciar
    LaunchedEffect(userId) {
        viewModel.cargarDatos(userId)
    }

    // Actualizar campos cuando lleguen los datos
    LaunchedEffect(datosState) {
        if (datosState is MisDatosState.Success) {
            val perfil = (datosState as MisDatosState.Success).perfil
            nombre = perfil.nombre ?: ""
            apellido = perfil.apellido ?: ""
            telefono = perfil.telefono ?: ""
            alias = perfil.alias
            email = perfil.email
        }
    }

    // Manejar éxito de actualización
    var showSuccessMessage by remember { mutableStateOf(false) }

    LaunchedEffect(actualizarState) {
        if (actualizarState is ActualizarPerfilState.Success) {
            showSuccessMessage = true
            kotlinx.coroutines.delay(2000) // Mostrar por 2 segundos
            showSuccessMessage = false
            viewModel.resetActualizarState()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // Header curvo
            CurvedHeader(
                title = "",
                icon = Icons.Default.ArrowBack,
                headerColor = OrangeDark,
                circleColor = OrangeDark,
                onBack = { navController.popBackStack() },
                height = 140.dp
            )

            // Avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-160).dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier
                        .size(95.dp)
                        .clip(CircleShape),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = BlueDark.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }

            // Título
            Text(
                text = "Mis datos",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = BlueDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .offset(y = (-150).dp)
            )

            // Contenido según el estado
            when (datosState) {
                is MisDatosState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .offset(y = (-130).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrangeDark)
                    }
                }

                is MisDatosState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .offset(y = (-130).dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (datosState as MisDatosState.Error).message,
                            color = Color.Red,
                            fontFamily = Poppins
                        )
                    }
                }

                is MisDatosState.Success -> {
                    // Formulario de datos
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .offset(y = (-130).dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Campo Nombre
                        MisDatosTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = "Nombre",
                            placeholder = "Ingresa tu nombre"
                        )

                        // Campo Apellido
                        MisDatosTextField(
                            value = apellido,
                            onValueChange = { apellido = it },
                            label = "Apellido",
                            placeholder = "Ingresa tu apellido"
                        )

                        // Campo Alias (solo lectura)
                        MisDatosTextField(
                            value = alias,
                            onValueChange = { },
                            label = "Alias",
                            placeholder = "Alias del usuario",
                            readOnly = true
                        )

                        // Campo Email (solo lectura)
                        MisDatosTextField(
                            value = email,
                            onValueChange = { },
                            label = "Email",
                            placeholder = "Email del usuario",
                            readOnly = true
                        )

                        // Campo Teléfono
                        MisDatosTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = "Celular",
                            placeholder = "Ingresa tu teléfono",
                            keyboardType = KeyboardType.Phone
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón Guardar
                        AppButton(
                            text = if (actualizarState is ActualizarPerfilState.Loading) "Guardando..." else "Guardar",
                            onClick = {
                                viewModel.actualizarPerfil(userId, nombre, apellido, telefono)
                            },
                            primary = true,
                            enabled = actualizarState !is ActualizarPerfilState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )

                        // Mensaje de éxito
                        if (showSuccessMessage) {
                            Text(
                                text = "✅ Datos actualizados correctamente",
                                color = Color(0xFF4CAF50), // Verde
                                fontFamily = Poppins,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                                    .padding(top = 8.dp)
                            )
                        }

                        // Mostrar error de actualización si existe
                        if (actualizarState is ActualizarPerfilState.Error) {
                            Text(
                                text = (actualizarState as ActualizarPerfilState.Error).message,
                                color = Color.Red,
                                fontFamily = Poppins,
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }

                        // Espacio final para que el botón no quede muy abajo
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MisDatosTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = BlueDark,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.Gray,
                    fontFamily = Poppins
                )
            },
            readOnly = readOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (readOnly) Color.Gray else OrangeDark,
                unfocusedBorderColor = Color.LightGray,
                disabledBorderColor = Color.Gray,
                focusedTextColor = BlueDark,
                unfocusedTextColor = if (readOnly) Color.Gray else BlueDark
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}