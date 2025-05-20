package com.example.saborchef.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.ui.theme.SaborChefTheme
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.res.painterResource
import com.example.saborchef.R

@Composable
fun ContactUsScreen(
    onBack: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera curvada
            CurvedHeader(
                title = "¿Querés comunicarte?",
                icon = Icons.Default.Phone,
                headerColor = OrangeDark,
                circleColor = androidx.compose.ui.graphics.Color.White,
                onBack = onBack
            )

            // Contenido desplazable
            Box(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Ítems con íconos vectoriales
                    RectangleItem(icon = Icons.Default.PhoneAndroid, text = "+54 9 11 6555-7899")
                    RectangleItem(icon = Icons.Default.MailOutline, text = "saborchef@gmail.com")

                    // Ítem con imagen PNG
                    RectangleItem(imageResId = R.drawable.icon_instagram, text = "@saborchef")
                }

            }
        }
    }
}

@Composable
fun RectangleItem(
    text: String,
    icon: ImageVector? = null,
    imageResId: Int? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            when {
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BlueDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
                imageResId != null -> {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontFamily = Poppins,
                fontSize = 16.sp,
                color = BlueDark,
                fontWeight = FontWeight.Medium
            )
        }
    }
}




@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ContactUsScreenPreview() {
    SaborChefTheme {
        Surface {
            ContactUsScreen(
                onBack = { /* Volver */ }
            )
        }
    }
}