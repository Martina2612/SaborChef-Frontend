package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.Poppins

@Composable
fun PasswordRecoveryScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit
) {
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
            text = "Recuperación de contraseña",
            fontSize = 20.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            color = BlueDark
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Ingresa tu mail para recuperar la contraseña",
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = BlueLight
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier
                .padding(horizontal=20.dp),
            placeholder = { Text(text="Correo electrónico", fontFamily = Poppins, fontWeight = FontWeight.Medium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = Color.Gray
                )
            },
            textStyle = TextStyle(
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = BlueDark
            ),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF1C2D57),
                unfocusedBorderColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        AppButton(
            text = "Enviar",
            onClick = onSendClick,
            primary = true,
            modifier = Modifier.padding(horizontal = 55.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordRecoveryScreen() {
    var email by remember { mutableStateOf("syafrilchoirul17@gmail.com") }

    PasswordRecoveryScreen(
        email = email,
        onEmailChange = { email = it },
        onSendClick = {},
        onBackClick = {}
    )
}