package com.example.saborchef.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.components.CurvedTextAroundCircle
import com.example.saborchef.ui.theme.Poppins

@Composable
fun DishWithLabel(
    imageRes: Int,
    label: String,
    imageOffsetX: Dp,
    imageOffsetY: Dp,
    textOffsetX: Dp = 0.dp,           // ← desfase X para el texto
    textOffsetY: Dp = 0.dp,           // ← desfase Y para el texto
    startAngle: Float,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(170.dp)
            .offset(x = imageOffsetX, y = imageOffsetY)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // 1) Imagen circular
        Image(
            painter = painterResource(imageRes),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
        )

        // 2) Texto curvo con offset independiente
        CurvedTextAroundCircle(
            text = label,
            modifier = Modifier
                .size(150.dp)                         // mismo tamaño que la imagen
                .offset(x = textOffsetX, y = textOffsetY),
            radius = 200f,
            centerX = 60f,
            centerY = 60f,
            startAngle = startAngle,
            fontFamily = Poppins,
            fontSize = 20.sp,
            color = Color.White
        )
    }
}


