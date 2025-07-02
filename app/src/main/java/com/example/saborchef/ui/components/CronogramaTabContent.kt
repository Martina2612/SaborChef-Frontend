package com.example.saborchef.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.saborchef.model.Clase
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun CronogramaTabContent(clases: List<Clase>) {
    var claseExpandidaId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        clases.sortedBy { it.numeroClase }.forEach { clase ->
            val expandida = claseExpandidaId == clase.idClase

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable {
                        claseExpandidaId = if (expandida) null else clase.idClase
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Clase ${clase.numeroClase}: ${clase.titulo}",
                        fontSize = 16.sp,
                        color = Color(0xFF374957),
                        fontWeight = FontWeight.SemiBold
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFF374957)
                    )
                }

                if (expandida) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = clase.descripcion,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Divider(color = Color(0xFFE0E0E0), modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}


