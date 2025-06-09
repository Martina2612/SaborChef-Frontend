package com.example.saborchef.ui.components

import androidx.compose.foundation.layout.width

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.ui.theme.SaborChefTheme


@Composable
fun FileSlot(
    uri: Uri?,
    placeholder: String,
    onClickPlaceholder: () -> Unit,
    onRemove: () -> Unit
) {
    if (uri == null) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 100.dp)
                .border(
                    BorderStroke(1.dp, Color.LightGray),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onClickPlaceholder() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = placeholder.uppercase(),
                    color = Color.Gray,
                    fontFamily = Poppins,
                    fontSize = 8.sp
                )
            }
        }
    } else {
        Card(
            modifier = Modifier
                .size(width = 120.dp, height = 100.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = uri.lastPathSegment ?: "archivo",
                    fontFamily = Poppins,
                    color = BlueDark,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Eliminar",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRemove() }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FileSlotPreview() {
    SaborChefTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estado vacío
            FileSlot(
                uri = null,
                placeholder = "Subir archivo",
                onClickPlaceholder = { /* Acción de prueba */ },
                onRemove = {}
            )

            // Estado con archivo cargado
            FileSlot(
                uri = Uri.parse("content://user/dni_front.jpg"),
                placeholder = "Subir archivo",
                onClickPlaceholder = {},
                onRemove = { /* Acción de prueba */ }
            )
        }
    }
}