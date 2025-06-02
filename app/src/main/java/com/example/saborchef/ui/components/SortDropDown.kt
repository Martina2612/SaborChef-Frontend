// File: app/src/main/java/com/example/saborchef/ui/components/SortDropdown.kt
package com.example.saborchef.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins

@Composable
fun SortDropdown(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Si selected está vacío, mostramos el placeholder "Ordenar por"
    val displayText = if (selected.isBlank()) "Ordenar por" else selected

    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BlueDark, RoundedCornerShape(18.dp))
            .clickable { expanded = true }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayText,
                fontFamily = Poppins,
                fontSize = 14.sp,
                fontWeight = if (selected.isBlank()) FontWeight.Normal else FontWeight.Medium,
                color = if (selected.isBlank()) Color.Gray else BlueDark
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Abrir menú",
                tint = BlueDark
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onSelected(option)
                }) {
                    Text(
                        text = option,
                        fontFamily = Poppins,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
