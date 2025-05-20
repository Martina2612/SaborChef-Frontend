package com.example.saborchef.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.Black
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.ui.theme.Typography

@Composable
fun RoundedWhiteButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),

        border = BorderStroke(1.dp, Color.Black),

        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor   = Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = text,
            style = Typography.labelSmall.copy(
                fontFamily = Poppins,
                color = Black,
                fontSize   = 16.sp
            )
        )
    }
}
