package com.example.saborchef.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.saborchef.R

@Composable
fun SuccessfulRegisterScreen(
    onContinue: () -> Unit
) {
    SuccessScreen(
        title = "Registro exitoso!",
        subtitle = "",
        buttonText = "Volver",
        imageResId = R.drawable.icon_check,
        onButtonClick = onContinue
    )
}

@Preview(showBackground = true)
@Composable
fun SuccessfulRegisterPreview() {
    SuccessfulRegisterScreen(onContinue = {})
}
