package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.Poppins

@Composable
fun NewPasswordScreen(
    password: String,
    onPasswordChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val passwordVisible = remember { mutableStateOf(false) }

    IconButton(
        onClick = onBackClick,
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
    Spacer(modifier = Modifier.height(64.dp))

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Crea tu nueva contraseña",
            fontFamily = Poppins,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = BlueDark
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ingresa tu nueva contraseña",
            fontFamily = Poppins,
            fontSize = 16.sp,
            color = BlueLight
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("Nueva contraseña", fontFamily = Poppins, fontWeight = FontWeight.Medium, color = Color.LightGray) },
            modifier = Modifier.padding(horizontal=20.dp),
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color.Gray)
            },
            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                }
            },
            textStyle = TextStyle(
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = BlueDark
            ),
            shape = RoundedCornerShape(20.dp),
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1C2D57),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.align(Alignment.Start).padding(start=56.dp)) {
            Text(
                text="Tu contraseña debe contener: ",
                fontFamily = Poppins,
                fontWeight= FontWeight.SemiBold,
                color=BlueDark
            )
            Spacer(modifier = Modifier.height(12.dp))
            RequirementItem(text = "Al menos 6 caracteres", valid = password.length >= 6)
            RequirementItem(text = "Al menos un número", valid = password.any { it.isDigit() })
        }

        Spacer(modifier = Modifier.height(120.dp))

        AppButton(
            text = "Listo",
            onClick = onSubmitClick,
            primary = true,
            modifier = Modifier.padding(horizontal = 55.dp)
        )
    }
}

@Composable
fun RequirementItem(text: String, valid: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (valid) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (valid) Color(0xFF27AE60) else Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (valid) Color(0xFF27AE60) else Color.LightGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNewPasswordScreen() {
    var password by remember { mutableStateOf("MOpa12") }

    NewPasswordScreen(
        password = password,
        onPasswordChange = { password = it },
        onSubmitClick = {},
        onBackClick = {}
    )
}
