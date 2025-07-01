package com.example.saborchef.ui.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.saborchef.ui.theme.Orange
import com.example.saborchef.model.CursoInscripto

@Composable
fun CursoCard(curso: CursoInscripto, mostrarBarraProgreso: Boolean = false, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(curso.imagenUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xDD333333)) // gris semitransparente
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        curso.nombreCurso,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .background(Orange, shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = curso.nivel,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (mostrarBarraProgreso) {
                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                    LinearProgressIndicator(
                        progress = curso.progreso / 100f,
                        color = Orange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${curso.progreso.toInt()}% completado",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

