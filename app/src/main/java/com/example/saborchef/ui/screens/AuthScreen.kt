package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.R
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins

@Composable
fun AuthScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {


            // Logo
            Text(
                text = "SaborChef",
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp,
                color = OrangeDark,           // color de tu logo
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Imagen ilustrativa
            Image(
                painter = painterResource(R.drawable.chef_welcome),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            // Mensaje principal
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "¿Listo para cocinar?",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp,
                    color = BlueDark
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Explora recetas deliciosas con videos paso a paso y lista de ingredientes detallados.",
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    color = BlueDark.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier= Modifier.padding(start = 24.dp, end = 24.dp)
                )
            }

            // Botones
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cada botón ocupa la mitad del ancho menos el espacio
                AppButton(
                    text = "Iniciar sesión",
                    onClick = onLogin,
                    primary = true,
                    modifier = Modifier.weight(1f)
                )
                AppButton(
                    text = "Registrarse",
                    onClick = onRegister,
                    primary = false,
                    modifier = Modifier.weight(1f)
                )
            }

            // Link "Volver al inicio"
            Text(
                text = "Volver al inicio",
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = OrangeDark,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .clickable { onBack() }
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp  = 432,    // 1080px / (400/160) ≈ 432dp
    heightDp = 963,    // 2408px / (400/160) ≈ 963dp
    name     = "AuthScreen - Galaxy A13 (432×963dp)"
)
@Composable
fun AuthScreen_GalaxyA13_Preview() {
    AuthScreen(
        onLogin    = {},
        onRegister = {},
        onBack     = {}
    )
}

