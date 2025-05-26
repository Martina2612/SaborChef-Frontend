package com.example.saborchef.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.ui.components.CurvedTextAroundCircle
import com.example.saborchef.ui.theme.Poppins
import androidx.compose.foundation.shape.CircleShape

@Composable
fun DishWithLabel(
    imageUrl: String,
    label: String,
    imageOffsetX: Dp,
    imageOffsetY: Dp,
    textOffsetX: Dp = 0.dp,
    textOffsetY: Dp = 0.dp,
    startAngle: Float,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(140.dp)
            .offset(x = imageOffsetX, y = imageOffsetY)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
        )

        CurvedTextAroundCircle(
            text = label,
            modifier = Modifier
                .size(120.dp)
                .offset(x = textOffsetX, y = textOffsetY),
            radius = 140f,
            centerX = 50f,
            centerY = 50f,
            startAngle = startAngle,
            fontFamily = Poppins,
            fontSize = 20.sp,
            color = Color.White
        )
    }
}
