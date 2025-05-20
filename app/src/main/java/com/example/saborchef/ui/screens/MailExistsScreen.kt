package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.R

@Composable
fun MailExistsScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal=30.dp,vertical=4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon_check),
                contentDescription = "Check",
                modifier = Modifier.size(120.dp)
            )

            Spacer(Modifier.height(32.dp))
            Text(
                text = "Ya existe el mail ingresado",
                style = TextStyle(fontFamily = Poppins, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlueDark)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Te ayudaremos a recuperar tu contraseña",
                style = TextStyle(fontFamily = Poppins, fontSize = 14.sp, color = BlueDark),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(100.dp))
            AppButton(
                text = "Siguiente",
                onClick = onContinue
            )
        }
    }}

@Preview(showBackground = true)
@Composable
fun MailExistsScreenPreview() {
    MailExistsScreen(
        onBack = {}, onContinue = {})
}
