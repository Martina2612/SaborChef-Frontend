package com.example.saborchef.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import kotlinx.coroutines.delay
import kotlin.String

@Composable
fun VerificationCodeScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onResendCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    var code by remember { mutableStateOf(List(4) { "" }) }
    var timeLeft by remember { mutableLongStateOf(86400L) } // 24hs en segundos

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        onResendCode() // Se llama cuando se acaba el tiempo
    }

    val timeFormatted = String.format(
        "%02d:%02d:%02d",
        timeLeft / 3600,
        (timeLeft % 3600) / 60,
        timeLeft % 60
    )

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
            .padding(horizontal=24.dp,vertical=6.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Revisa tu mail",
                style = TextStyle(
                    fontFamily = Poppins,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark
                )
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Por favor, ingresa el código que enviamos a tu mail (duración max. 24hs)",
                style = TextStyle(fontFamily = Poppins, fontSize = 14.sp, color=BlueDark),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                code.forEachIndexed { i, char ->
                    OutlinedTextField(
                        value = char,
                        onValueChange = { value ->
                            if (value.length <= 1 && value.all { it.isDigit() }) {
                                code = code.toMutableList().also { it[i] = value }
                            }
                        },
                        modifier = Modifier
                            .width(60.dp)
                            .height(64.dp),
                        textStyle = TextStyle(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
            Text(
                text = "El código expira en $timeFormatted",
                color = if (timeLeft < 60) Color.Red else BlueDark,
                fontFamily = Poppins
            )

            Spacer(Modifier.height(40.dp))
            AppButton(
                text = "Siguiente",
                onClick = onNext
            )

            Spacer(Modifier.height(40.dp))
            AppButton(
                text = "Enviarlo nuevamente",
                onClick = onResendCode,
                primary = false
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VerificationCodePreview() {
    VerificationCodeScreen(
        onBack = {},
        onNext = {},
        onResendCode = {}
    )
}
