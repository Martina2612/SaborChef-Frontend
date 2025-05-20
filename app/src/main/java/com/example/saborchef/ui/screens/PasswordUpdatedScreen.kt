package com.example.saborchef.ui.screens

import androidx.compose.runtime.Composable
import com.example.saborchef.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PasswordUpdatedScreen(onNavigateHome: () -> Unit) {
    SuccessScreen(
        title = "Se ha actualizado\ntu contraseña!",
        subtitle = "",
        buttonText = "Volver al inicio",
        imageResId = R.drawable.icon_check,
        onButtonClick = onNavigateHome
    )
}

@Preview(showBackground = true)
@Composable
fun PasswordUpdatedScreenPreview() {
    PasswordUpdatedScreen(onNavigateHome = {})
}
