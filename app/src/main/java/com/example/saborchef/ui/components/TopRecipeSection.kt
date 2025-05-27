package com.example.saborchef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.saborchef.models.TopRecetaResponse
import com.example.saborchef.ui.theme.BlueDark
import kotlinx.coroutines.launch

@Composable
fun TopCarouselSection(
    title: String,
    items: List<TopRecetaResponse>,
    onClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BlueDark
            ),
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        if (items.isEmpty()) {
            Text(
                text = "No hay recetas",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
            return
        }

        val pagerState = rememberPagerState(pageCount = { items.size })
        val scope = rememberCoroutineScope()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp
        ) { page ->
            val receta = items[page]
            val imageUrl = receta.fotoPrincipal ?: ""
            val nombre = receta.nombreReceta ?: ""
            val id = receta.idReceta ?: 0L

            Box(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onClick(id) }
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = nombre,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xAA000000)),
                                startY = 0f,
                                endY = 300f
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = nombre,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
