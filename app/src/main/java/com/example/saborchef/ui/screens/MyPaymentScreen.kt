package com.example.saborchef.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.components.PaymentCardSection
import com.example.saborchef.ui.theme.SaborChefTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.ui.tooling.preview.Preview
import com.example.saborchef.ui.theme.OrangeDark

/**
 * Pantalla “Mis medios de pago” (EditPaymentScreen):
 * - Muestra los datos que el usuario ya guardó.
 * - Cada campo tiene su propio lápiz para editar “in situ” ese campo.
 * - Al pulsar “Confirmar” se guardan todos los cambios y se deshabilitan los campos otra vez.
 *
 * @param existingCardNumber       Valor inicial que trae el servidor/DB para número de tarjeta.
 * @param existingSecurityCode     Valor inicial para código de seguridad.
 * @param existingExpiryDate       Valor inicial para vencimiento.
 * @param existingCardHolderName   Valor inicial para nombre del titular.
 *
 * @param onBack   Lambda que se dispara cuando el usuario toca “volver” en la cabecera.
 * @param onSave   Lambda que recibe todos los valores finales cuando el usuario confirma
 *                 la edición (para que la app los mande al repositorio/servidor).
 */
@Composable
fun EditPaymentScreen(
    existingCardNumber: String,
    existingSecurityCode: String,
    existingExpiryDate: String,
    existingCardHolderName: String,
    onBack: () -> Unit,
    onSave: (
        newCardNumber: String,
        newSecurityCode: String,
        newExpiryDate: String,
        newCardHolderName: String
    ) -> Unit
) {
    // === Estados hoisted de cada valor ===
    var cardNumber by remember { mutableStateOf(existingCardNumber) }
    var securityCode by remember { mutableStateOf(existingSecurityCode) }
    var expiryDate by remember { mutableStateOf(existingExpiryDate) }
    var cardHolderName by remember { mutableStateOf(existingCardHolderName) }

    // === Estados hoisted para saber si ese campo está habilitado (editable) o no ===
    var isCardNumberEditable by remember { mutableStateOf(false) }
    var isSecurityCodeEditable by remember { mutableStateOf(false) }
    var isExpiryDateEditable by remember { mutableStateOf(false) }
    var isCardHolderNameEditable by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // --- Cabecera curvada ---
        CurvedHeader(
            title = "Mis medios de pago",
            icon = Icons.Default.CreditCard,
            headerColor = OrangeDark,
            circleColor = androidx.compose.ui.graphics.Color.White,
            onBack = onBack
        )


        // --- Contenedor principal ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card blanca que envuelve todos los campos
            // --- Aquí reutilizamos PaymentCardSection con todos los estados hoisted ---
                PaymentCardSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),

                    // “Número de tarjeta”
                    cardNumberValue = cardNumber,
                    onCardNumberChange = { cardNumber = it },
                    isCardNumberEditable = isCardNumberEditable,
                    onCardNumberEditClick = { isCardNumberEditable = true },

                    // “Código de seguridad”
                    securityCodeValue = securityCode,
                    onSecurityCodeChange = { securityCode = it },
                    isSecurityCodeEditable = isSecurityCodeEditable,
                    onSecurityCodeEditClick = { isSecurityCodeEditable = true },

                    // “Vencimiento”
                    expiryDateValue = expiryDate,
                    onExpiryDateChange = { expiryDate = it },
                    isExpiryDateEditable = isExpiryDateEditable,
                    onExpiryDateEditClick = { isExpiryDateEditable = true },

                    // “Nombre del titular”
                    cardHolderNameValue = cardHolderName,
                    onCardHolderNameChange = { cardHolderName = it },
                    isCardHolderNameEditable = isCardHolderNameEditable,
                    onCardHolderNameEditClick = { isCardHolderNameEditable = true },

                    showEditIcons = true,
                )


            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón Confirmar / Guardar cambios ---
            AppButton(
                text = "Confirmar",
                onClick = {
                    // Al confirmar: deshabilitamos todos los campos
                    isCardNumberEditable = false
                    isSecurityCodeEditable = false
                    isExpiryDateEditable = false
                    isCardHolderNameEditable = false

                    // Disparamos onSave con los valores finales
                    onSave(cardNumber, securityCode, expiryDate, cardHolderName)
                },
                primary = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun EditPaymentPreview() {
    SaborChefTheme {
        Surface {
            EditPaymentScreen(
                existingCardNumber = "1234-5678-9012-3456",
                existingSecurityCode = "123",
                existingExpiryDate = "12/2025",
                existingCardHolderName = "VISA",
                onBack = { /* Volver */ },
                onSave = { _, _, _, _ -> /* Guardar cambios */ }
            )
        }
    }
}
