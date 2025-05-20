package com.example.saborchef.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins

/**
 * Componente común para mostrar los campos de una tarjeta.
 * Se parametriza para que el padre (pantalla) tenga el control exacto de:
 *   - El valor de cada campo
 *   - Si cada campo está habilitado o no (hoisted)
 *   - El callback que se dispara al tocar el ícono de lápiz (para pasar a modo edición)
 *
 * @param cardNumberValue          Valor actual del campo "Número de tarjeta".
 * @param onCardNumberChange       Callback que actualiza el valor de "Número de tarjeta".
 * @param isCardNumberEditable     Bandera que indica si "Número de tarjeta" está habilitado.
 * @param onCardNumberEditClick    Callback que se dispara al tocar el lápiz de ese campo.
 *
 * @param securityCodeValue        Valor actual del campo "Código de seguridad".
 * @param onSecurityCodeChange     Callback que actualiza el valor de "Código de seguridad".
 * @param isSecurityCodeEditable   Bandera que indica si "Código de seguridad" está habilitado.
 * @param onSecurityCodeEditClick  Callback que se dispara al tocar el lápiz de ese campo.
 *
 * @param expiryDateValue          Valor actual del campo "Vencimiento".
 * @param onExpiryDateChange       Callback que actualiza el valor de "Vencimiento".
 * @param isExpiryDateEditable     Bandera que indica si "Vencimiento" está habilitado.
 * @param onExpiryDateEditClick    Callback que se dispara al tocar el lápiz de ese campo.
 *
 * @param cardHolderNameValue      Valor actual del campo "Nombre del titular".
 * @param onCardHolderNameChange   Callback que actualiza el valor de "Nombre del titular".
 * @param isCardHolderNameEditable Bandera que indica si "Nombre del titular" está habilitado.
 * @param onCardHolderNameEditClickCallback Callback que se dispara al tocar el lápiz de ese campo.
 *
 * @param showEditIcons            Si es false, oculta todos los íconos de lápiz y fuerza todos los campos a enabled=true.
 * @param modifier                 Modifier opcional para posicionar / tamaño del Card completo.
 * @param icon                     Ícono que se muestra arriba (por defecto, ícono de tarjeta).
 * @param title                    Título que aparece en el componente (por defecto "Tarjeta de Crédito").
 */
@Composable
fun PaymentCardSection(
    // === Parámetros para "Número de tarjeta" ===
    cardNumberValue: String,
    onCardNumberChange: (String) -> Unit,
    isCardNumberEditable: Boolean,
    onCardNumberEditClick: () -> Unit,

    // === Parámetros para "Código de seguridad" ===
    securityCodeValue: String,
    onSecurityCodeChange: (String) -> Unit,
    isSecurityCodeEditable: Boolean,
    onSecurityCodeEditClick: () -> Unit,

    // === Parámetros para "Vencimiento" ===
    expiryDateValue: String,
    onExpiryDateChange: (String) -> Unit,
    isExpiryDateEditable: Boolean,
    onExpiryDateEditClick: () -> Unit,

    // === Parámetros para "Nombre del titular" ===
    cardHolderNameValue: String,
    onCardHolderNameChange: (String) -> Unit,
    isCardHolderNameEditable: Boolean,
    onCardHolderNameEditClick: () -> Unit,

    // === Parámetros de presentación ===
    showEditIcons: Boolean = true,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.CreditCard,
    title: String = "Tarjeta de Crédito"
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Ícono naranja + título centrado ---
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = BlueDark
            )

            // === Campo: Número de tarjeta ===
            LabeledTextField(
                label = "Número de tarjeta",
                placeholder = "XXXX-XXXX-XXXX-XXXX",
                value = cardNumberValue,
                onValueChange = onCardNumberChange,
                isEnabled = if (showEditIcons) isCardNumberEditable else true,
                onEditClick = onCardNumberEditClick.takeIf { showEditIcons },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // === Fila: Código y Vencimiento ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Código de seguridad
                LabeledTextField(
                    label = "Código de seguridad",
                    placeholder = "000",
                    value = securityCodeValue,
                    onValueChange = onSecurityCodeChange,
                    isEnabled = if (showEditIcons) isSecurityCodeEditable else true,
                    onEditClick = onSecurityCodeEditClick.takeIf { showEditIcons },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                // Vencimiento
                LabeledTextField(
                    label = "Vencimiento",
                    placeholder = "MM/AAAA",
                    value = expiryDateValue,
                    onValueChange = onExpiryDateChange,
                    isEnabled = if (showEditIcons) isExpiryDateEditable else true,
                    onEditClick = onExpiryDateEditClick.takeIf { showEditIcons },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // === Campo: Nombre completo del titular ===
            LabeledTextField(
                label = "Nombre completo de tu tarjeta",
                placeholder = "VISA, MASTERCARD, ETC",
                value = cardHolderNameValue,
                onValueChange = onCardHolderNameChange,
                isEnabled = if (showEditIcons) isCardHolderNameEditable else true,
                onEditClick = onCardHolderNameEditClick.takeIf { showEditIcons }
            )
        }
    }
}


/**
 * Un solo campo con etiqueta (label), placeholder y opcionalmente un ícono de lápiz.
 * - Si [onEditClick] es non-null, muestra el ícono de lápiz a la derecha; al tocarlo, dispara esa lambda.
 * - [isEnabled] controla si el campo está habilitado para editar o no.
 *
 * @param label            Texto de la etiqueta que se muestra arriba del TextField.
 * @param placeholder      Placeholder que se ve cuando está vacío.
 * @param value            Valor actual del TextField (hoisted state).
 * @param onValueChange    Callback que actualiza `value`.
 * @param isEnabled        Si es true, el usuario puede escribir; si es false, queda read-only.
 * @param onEditClick      Si viene un `() -> Unit`, se muestra el lápiz. Cuando el campo está deshabilitado, el lápiz aparece en color naranja y al tocarlo dispara esta lambda. Si está habilitado, el lápiz se puede mostrar semitransparente.
 * @param modifier         Modifier para tamaño/ubicación.
 * @param keyboardOptions  KeyboardOptions para controlar tipo de teclado (números, texto, etc.).
 */
@Composable
private fun LabeledTextField(
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
            fontWeight = FontWeight.Medium,
            color = BlueDark,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
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
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = BlueDark.copy(alpha = 0.6f),
                unfocusedBorderColor = BlueDark.copy(alpha = 0.3f),
                cursorColor = Orange,
                placeholderColor = BlueDark.copy(alpha = 0.4f),
                textColor = BlueDark,
                backgroundColor = SolidColor(androidx.compose.ui.graphics.Color.Transparent).value
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            trailingIcon = {
                // Si onEditClick es non-null → mostramos lápiz
                if (onEditClick != null) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = if (isEnabled) "Editar" else "Habilitar edición",
                        tint = if (isEnabled) BlueDark.copy(alpha = 0.6f) else Orange,
                        modifier = Modifier.clickable {
                            if (!isEnabled) {
                                onEditClick()
                            }
                        }
                    )
                }
            }
        )
    }
}
