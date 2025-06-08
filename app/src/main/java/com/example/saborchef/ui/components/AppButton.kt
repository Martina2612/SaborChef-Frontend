package com.example.saborchef.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import androidx.compose.ui.graphics.Color

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    primary: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val container = if (primary) OrangeDark else Color(0xFFF0EFEF)
    val content   = if (primary) Color.White else BlueDark

    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = container,
            contentColor = content
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontFamily = Poppins,
            color = content
        )
    }
}

