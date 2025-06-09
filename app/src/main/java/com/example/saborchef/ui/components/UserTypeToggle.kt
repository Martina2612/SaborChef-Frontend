package com.example.saborchef.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins
import androidx.compose.ui.text.font.FontWeight
import com.example.saborchef.ui.theme.OrangeDark

@Composable
fun UserTypeToggle(
    selected: String,
    onSelect: (String) -> Unit
) {
    val types = listOf("Alumno", "Usuario")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFFF0F0F3), shape = RoundedCornerShape(24.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        types.forEach { type ->
            val isSelected = selected.equals(type, ignoreCase = true)

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) OrangeDark else Color.Transparent
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else BlueDark
            )
            val elevation by animateDpAsState(
                targetValue = if (isSelected) 4.dp else 0.dp
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .shadow(
                        elevation = elevation,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color.LightGray,
                        spotColor = Color.Gray
                    )
                    .clickable { onSelect(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}
