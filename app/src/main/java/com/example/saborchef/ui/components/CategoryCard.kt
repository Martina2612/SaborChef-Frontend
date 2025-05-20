// File: ui/components/CategoryCard.kt

package com.example.saborchef.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.Poppins

/**
 * @param label       Texto de la categoría
 * @param imagePainter  Imagen de fondo (puede venir de painterResource(...) o de Coil)
 * @param size        Tamaño de cada card (ancho = fillMaxWidth si no se pasa, alto = size)
 * @param onClick     Callback al tocar
 */
@Composable
fun CategoryCard(
    label: String,
    imagePainter: Painter,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        modifier = Modifier
            .clickable { onClick(label) }
    ) {
        Box {
            // Fondo: la imagen
            Image(
                painter = imagePainter,
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Capa semitransparente para mejorar legibilidad del texto
            Surface(
                color = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0x80000000))
                ).let { brush -> Color.Unspecified } /* Placeholder! */,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0x80000000))))
            ) {}

            // Texto centrado abajo
            Text(
                text = label,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                fontFamily = Poppins,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}
