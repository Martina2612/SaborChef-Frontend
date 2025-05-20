package com.example.saborchef.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.saborchef.ui.theme.Poppins

@Composable
fun SortDropdown(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .padding(end = 16.dp)
        .clickable { expanded = true }
    ) {
        Text(
            text = selected,
            fontFamily = Poppins,
            modifier = Modifier.padding(8.dp)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onSelected(option)
                }) {
                    Text(option, fontFamily = Poppins)
                }
            }
        }
    }
}
