// src/main/java/com/example/saborchef/ui/components/PhotosCarousel.kt
package com.example.saborchef.ui.components

import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins

@Composable
fun PhotosCarousel(
    photos: List<Uri>,
    onRemove: (Int) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Fotos de la receta",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = BlueDark,
                fontSize= 18.sp
            ),

            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Contamos un extra para el slot de “añadir foto”
        val pageCount = photos.size + 1
        val pagerState: PagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { pageCount }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = 48.dp),
            pageSpacing = 12.dp
        ) { page ->
            val isAddSlot = page == photos.size
            val isCurrent = page == pagerState.currentPage
            val scale by animateFloatAsState(if (isCurrent) 1.1f else 0.9f)

            Card(
                modifier = Modifier
                    .scale(scale)
                    .width(280.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        if (isAddSlot) onAddClick()
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                border = if (isAddSlot)
                    BorderStroke(1.dp, Color.LightGray)
                else
                    null,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (isAddSlot) {
                    // Tarjeta “Agregar foto”
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 1.dp,
                                color = BlueDark,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.CameraAlt,
                                contentDescription = "Agregar foto",
                                tint = Color.LightGray,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Añade una foto",
                                color = Color.LightGray,
                                fontSize = 10.sp,
                                fontFamily = Poppins
                            )
                        }
                    }
                }else {
                    // Tarjeta de foto existente
                    Box {
                        AsyncImage(
                            model = photos[page],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                        )
                        IconButton(
                            onClick = { onRemove(page) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.7f), shape = RoundedCornerShape(50))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Eliminar",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
