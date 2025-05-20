package com.example.saborchef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.Poppins
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight
import com.example.saborchef.ui.theme.BlueDark

@Composable
fun CurvedHeader(
    title: String,
    icon: ImageVector,
    headerColor: Color,
    circleColor: Color,
    onBack: () -> Unit,
    height: Dp = 140.dp
) {
    val circleSize = 90.dp

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
        ) {
            // Fondo curvo
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, h * 0.75f)
                    quadraticBezierTo(w / 2, h * 0.55f, w, h * 0.75f)
                    lineTo(w, 0f)
                    close()
                }
                drawPath(path, color = headerColor)
            }

            // Icono de volver
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .size(28.dp)
                    .align(Alignment.TopStart)
                    .clickable { onBack() }
            )

            // Círculo con ícono y título juntos, en el centro
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = circleSize / 2) // superposición hacia abajo
            ) {
                Card(
                    shape = CircleShape,
                    elevation = 8.dp,
                    backgroundColor = circleColor,
                    modifier = Modifier.size(circleSize)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = BlueDark,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // Separación entre ícono y título

                Text(
                    text = title,
                    color = BlueDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = Poppins
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Separación entre header y contenido
    }
}

