package com.example.saborchef.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.saborchef.R
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.ui.theme.Poppins

@Composable
fun RecipeCard(
    id: String,
    title: String,
    imageUrl: Uri,
    duration: String,
    portions: Int,
    rating: Int,
    user: String,
    onClick: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        backgroundColor = androidx.compose.ui.graphics.Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick(id) }
    ) {
        Row(Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Título y rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontFamily = Poppins,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlueDark,
                        modifier = Modifier.weight(1f)
                    )

                }
                // Duración y porciones
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector=Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = BlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$duration",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = BlueDark
                    )
                    Spacer(Modifier.width(94.dp))
                    Icon(
                        imageVector = Icons.Default.PersonOutline,
                        contentDescription = null,
                        tint = BlueDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$portions porciones",
                        fontFamily = Poppins,
                        fontSize = 12.sp,
                        color = BlueDark
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                // Usuario
                Text(
                    text = user,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    color = BlueDark
                )
                    Spacer(Modifier.width(90.dp))
                    Row {
                        repeat(rating) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Orange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        repeat(5 - rating) {
                            Icon(
                                imageVector = Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = Orange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecipeCard() {
    RecipeCard(
        id = "1",
        title = "Ensalada de caballa y verduras",
        imageUrl = Uri.parse("android.resource://com.example.saborchef/" + R.drawable.img_step1),
        duration = "15 min",
        portions = 3,
        rating = 4,
        user = "usuario2612",
        onClick = {}
    )
}
