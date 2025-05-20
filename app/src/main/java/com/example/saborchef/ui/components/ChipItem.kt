package com.example.saborchef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ChipItem(
    text: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(Orange.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontFamily = Poppins,
                color = OrangeDark
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar",
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onRemove),
                tint = OrangeDark
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChipItem() {
    ChipItem(text = "Tomate", onRemove = {})
}
