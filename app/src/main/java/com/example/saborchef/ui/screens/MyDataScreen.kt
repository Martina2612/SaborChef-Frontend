package com.example.saborchef.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.SaborChefTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins

/**
 * Pantalla “Mis datos”:
 * - Muestra nombre, apellido, username, email y celular.
 * - Cada campo tiene lápiz para habilitar edición.
 * - Al “Guardar” se deshabilitan y se emite onSave con todos los datos.
 */
@Composable
fun MyDataScreen(
    existingNombre: String,
    existingApellido: String,
    existingUsername: String,
    existingEmail: String,
    existingCelular: String,
    onBack: () -> Unit,
    onSave: (
        nombre: String,
        apellido: String,
        username: String,
        email: String,
        celular: String
    ) -> Unit
) {
    // --- Estados de valor ---
    var nombre by remember { mutableStateOf(existingNombre) }
    var apellido by remember { mutableStateOf(existingApellido) }
    var username by remember { mutableStateOf(existingUsername) }
    var email by remember { mutableStateOf(existingEmail) }
    var celular by remember { mutableStateOf(existingCelular) }

    // --- Estados de edición ---
    var editNombre by remember { mutableStateOf(false) }
    var editApellido by remember { mutableStateOf(false) }
    var editUsername by remember { mutableStateOf(false) }
    var editEmail by remember { mutableStateOf(false) }
    var editCelular by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header curvo + avatar + título
        CurvedHeader(
            title = "Mis datos",
            icon = Icons.Default.Person,
            headerColor = OrangeDark,
            circleColor = Color.White,
            onBack = onBack
        )

        // Contenido
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Nombre
            LabeledTextField(
                label = "Nombre",
                placeholder = "Tu nombre",
                value = nombre,
                onValueChange = { nombre = it },
                isEnabled = editNombre,
                onEditClick = { editNombre = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Apellido
            LabeledTextField(
                label = "Apellido",
                placeholder = "Tu apellido",
                value = apellido,
                onValueChange = { apellido = it },
                isEnabled = editApellido,
                onEditClick = { editApellido = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username
            LabeledTextField(
                label = "Alias",
                placeholder = "tizoCrist2025",
                value = username,
                onValueChange = { username = it },
                isEnabled = !editUsername,
                onEditClick = { editUsername = false }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            LabeledTextField(
                label = "Email",
                placeholder = "email@email.com.ar",
                value = email,
                onValueChange = { email = it },
                isEnabled = editEmail,
                onEditClick = { editEmail = true },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Celular
            LabeledTextField(
                label = "Celular",
                placeholder = "11 1234-5678",
                value = celular,
                onValueChange = { celular = it },
                isEnabled = editCelular,
                onEditClick = { editCelular = true },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón Guardar
            AppButton(
                text = "Guardar",
                onClick = {
                    // Desactivar edición en todos los campos
                    editNombre = false
                    editApellido = false
                    editUsername = false
                    editEmail = false
                    editCelular = false
                    // Emitir guardado
                    onSave(nombre, apellido, username, email, celular)
                },
                primary = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}

@Composable
fun LabeledTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEnabled: Boolean,
    onEditClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BlueDark,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = BlueDark.copy(alpha = 0.4f)
                )
            },
            singleLine = true,
            enabled = isEnabled,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlueDark.copy(alpha = 0.6f),
                unfocusedBorderColor = BlueDark.copy(alpha = 0.3f),
                cursorColor = OrangeDark,
                placeholderColor = BlueDark.copy(alpha = 0.4f),
                textColor = BlueDark,
                backgroundColor = SolidColor(Color.Transparent).value
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            trailingIcon = {
                if (onEditClick != null) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = if (!isEnabled) OrangeDark else Color.Transparent,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                if (!isEnabled) onEditClick()
                            }
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun MisDatosPreview() {
    SaborChefTheme {
        Surface {
            MyDataScreen(
                existingNombre = "Juan",
                existingApellido = "Pérez",
                existingUsername = "tizoCrist2025",
                existingEmail = "email@email.com.ar",
                existingCelular = "11 1234-5678",
                onBack = {},
                onSave = { _, _, _, _, _ -> }
            )
        }
    }
}
