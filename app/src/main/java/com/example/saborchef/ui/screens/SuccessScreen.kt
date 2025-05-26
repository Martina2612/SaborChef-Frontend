package com.example.saborchef.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.ui.components.AppButton
import androidx.compose.material3.Text

@Composable
fun SuccessScreen(
    title: String,
    subtitle: String,
    buttonText: String,
    imageResId: Int,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                fontFamily = Poppins,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BlueDark,
                textAlign = TextAlign.Center
            )

            if (subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    fontFamily = Poppins,
                    fontSize = 14.sp,
                    color = BlueDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            AppButton(
                text = buttonText,
                onClick = onButtonClick
            )
        }
    }
}
