package com.example.saborchef.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.saborchef.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun RegistrationSuccessScreen(onNavigateHome: () -> Unit) {
    SuccessScreen(
        title = "¡Listo!",
        subtitle = "Ya podés ingresar a tu cuenta",
        buttonText = "Ir al inicio",
        imageResId = R.drawable.icon_check,
        onButtonClick = onNavigateHome
    )
}

@Preview(showBackground = true)
@Composable
fun RegistrationSuccessScreenPreview() {
    RegistrationSuccessScreen(onNavigateHome = {})
}
