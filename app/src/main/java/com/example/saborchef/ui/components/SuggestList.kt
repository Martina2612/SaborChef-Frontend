package com.example.saborchef.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins

@Composable
fun SuggestList(suggestions: List<String>, onSuggestionClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        suggestions.forEach { suggestion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionClick(suggestion) }
                    .padding(vertical = 8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = BlueDark
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = suggestion,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = BlueDark
                )
            }
            Divider()
        }
    }
}
