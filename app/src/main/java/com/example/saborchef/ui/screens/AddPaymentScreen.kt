package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.PaymentCardSection
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.ui.theme.SaborChefTheme

/**
 * Pantalla que se usa en el registro:
 * → Obligatorio ingresar un medio de pago (todos los campos editables desde el inicio).
 * @param onConfirm Callback que recibe un objeto con los datos ingresados.
 */
@Composable
fun AddPaymentScreen(
    onConfirm: (cardNumber: String, securityCode: String, expiryDate: String, tipoTarjeta: String) -> Unit
) {
    var cardNumber by remember { mutableStateOf("") }
    var securityCode by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var tipoTarjeta by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(OrangeDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ingresa un medio de pago para tus futuros cursos",
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = BlueDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        PaymentCardSection(
            modifier = Modifier.fillMaxWidth(),
            cardNumberValue = cardNumber,
            onCardNumberChange = { cardNumber = it },
            isCardNumberEditable = true,
            onCardNumberEditClick = {},

            securityCodeValue = securityCode,
            onSecurityCodeChange = { securityCode = it },
            isSecurityCodeEditable = true,
            onSecurityCodeEditClick = {},

            expiryDateValue = expiryDate,
            onExpiryDateChange = { expiryDate = it },
            isExpiryDateEditable = true,
            onExpiryDateEditClick = {},

            cardHolderNameValue = tipoTarjeta, // ahora representa tipoTarjeta
            onCardHolderNameChange = { tipoTarjeta = it },
            isCardHolderNameEditable = true,
            onCardHolderNameEditClick = {},

            showEditIcons = false
        )

        Spacer(modifier = Modifier.height(32.dp))

        AppButton(
            text = "Confirmar",
            onClick = {
                onConfirm(cardNumber, securityCode, expiryDate, tipoTarjeta)
            },
            primary = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}

