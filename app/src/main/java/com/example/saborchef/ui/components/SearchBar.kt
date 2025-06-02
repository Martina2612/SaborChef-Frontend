// File: app/src/main/java/com/example/saborchef/ui/components/SearchBar.kt
package com.example.saborchef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins

/**
 * @param query          Texto actual del campo de búsqueda
 * @param onQueryChange  Callback que se invoca en cada cambio de texto
 * @param onSearch       Callback que se invoca al pulsar la lupa
 * @param onFilterClick  Callback que se invoca al pulsar el icono de filtros
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,          // <-- agregamos este parámetro
    onFilterClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            androidx.compose.material.Text(
                "Busca una receta aquí",
                fontFamily = Poppins
            )
        },
        singleLine = true,
        leadingIcon = {
            IconButton(onClick = onSearch) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = BlueDark
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = "Filtrar",
                    tint = OrangeDark
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .padding(start = 8.dp, end = 8.dp, top = 10.dp),
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = BlueDark,
            unfocusedBorderColor = BlueLight,
            cursorColor = BlueDark
        ),
        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = Poppins)
    )
}
