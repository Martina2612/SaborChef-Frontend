package com.example.saborchef.ui.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins

/**
 * Slot de archivo que muestra un placeholder con etiqueta y un label, o la card con el nombre del archivo.
 */
@Composable
fun FileSlot(
    uri: Uri?,
    label: String,
    placeholder: String,
    onClickPlaceholder: () -> Unit,
    onRemove: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Label del slot
        Text(
            text = label,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = BlueDark,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (uri == null) {
            Box(
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onClickPlaceholder() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = placeholder,
                    color = Color.Gray,
                    fontFamily = Poppins,
                    fontSize = 10.sp
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .height(56.dp)
                    .width(200.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 2.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = uri.lastPathSegment ?: "archivo",
                        fontFamily = Poppins,
                        color = BlueDark,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(Modifier.width(8.dp))
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
}

@Preview(showBackground = true)
@Composable
fun FileSlotPreview() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FileSlot(
            uri = null,
            label = "DNI Frente",
            placeholder = "Subir foto",
            onClickPlaceholder = { /* prueba */ },
            onRemove = {}
        )
        FileSlot(
            uri = Uri.parse("content://user/dni_front.jpg"),
            label = "DNI Dorso",
            placeholder = "Subir foto",
            onClickPlaceholder = {},
            onRemove = { /* prueba */ }
        )
    }
}
