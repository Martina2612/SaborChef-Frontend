package com.example.saborchef.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.R
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.ui.theme.Poppins
import coil.compose.AsyncImage
import com.example.saborchef.ui.theme.BlueLight

enum class UserRole { ALUMNO, USUARIO }

data class ProfileOption(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun ProfileScreen(
    userName: String,
    photoUri: Uri?,
    role: UserRole,
    onBack: () -> Unit,
    onEditPhoto: () -> Unit,
    onOptionClick: (label: String) -> Unit,
    onBecomeStudent: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // 1) Header curvo; ocultamos su círculo interno
            CurvedHeader(
                title = "",
                icon = Icons.Default.ArrowBack,
                headerColor = OrangeDark,
                circleColor = OrangeDark,      // mismo que header
                onBack = onBack,
                height = 140.dp
            )

            // 2) Aquí superponemos nuestro avatar blanco
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-160).dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Círculo blanco para avatar
                Surface(
                    modifier = Modifier
                        .size(95.dp)
                        .clip(CircleShape)
                        .shadow(elevation = 4.dp, shape=CircleShape),
                    color = Color.White,
                    elevation = 4.dp
                ) {
                    if (photoUri != null) {
                        // si usas Coil pon AsyncImage; si no, un placeholder
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Placeholder",
                            tint = BlueDark.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        )
                    }
                }
                // Lápiz superpuesto
                IconButton(
                    onClick=onEditPhoto,
                    modifier = Modifier
                        .offset(x = (35).dp, y = (70).dp)
                        .size(24.dp)
                        .background(BlueLight, shape = CircleShape)
                ){
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar foto",
                        tint = BlueDark
                    )
                }
            }

            // 3) Nombre
            Text(
                text = userName,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = BlueDark,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .offset(y = (-150).dp)
            )

            // 4) Opciones (igual que antes, con ImageVector)
            val opciones = remember(role) {
                mutableListOf<ProfileOption>().apply {
                    add(ProfileOption(Icons.Default.Person,      "Mis datos")              { onOptionClick("Mis datos") })
                    add(ProfileOption(Icons.Default.Book, "Mis recetas")           { onOptionClick("Mis recetas") })
                    if (role == UserRole.ALUMNO) {
                        add(ProfileOption(Icons.Default.OndemandVideo,    "Mis cursos")             { onOptionClick("Mis cursos") })
                        add(ProfileOption(Icons.Default.Payment, "Medios de pago")         { onOptionClick("Medios de pago") })
                    }
                    add(ProfileOption(Icons.Default.OutlinedFlag, "Términos y condiciones") { onOptionClick("Términos y condiciones") })
                    add(ProfileOption(Icons.Default.Phone,       "Contáctanos")            { onOptionClick("Contáctanos") })
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y=(-130).dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                opciones.forEach { opt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                            .shadow(elevation = 1.dp,shape = RoundedCornerShape(8.dp))
                            .clickable { opt.onClick() }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = opt.icon,
                            contentDescription = opt.label,
                            tint = BlueDark
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = opt.label, fontFamily = Poppins, color = BlueDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5) Botón “¡Hazte alumno!” solo para USUARIO
            if (role == UserRole.USUARIO) {
                AppButton(
                    text = "¡Hazte alumno!",
                    onClick = onBecomeStudent,
                    primary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 24.dp)
                )
            }
        }

        // 6) Cerrar sesión fijo abajo
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Cerrar sesión",
                tint = BlueDark
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar sesión",
                fontFamily = Poppins,
                color = BlueDark
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewProfileAlumno() {
    SaborChefTheme {
        ProfileScreen(
            userName = "Tiziano Cristiani",
            photoUri = null, // o Uri.parse("...")
            role = UserRole.ALUMNO,
            onBack = {},
            onEditPhoto = { /* lanza galería/cámara */ },
            onOptionClick = {},
            onBecomeStudent = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewProfileUsuario() {
    SaborChefTheme {
        ProfileScreen(
            userName = "Tiziano Cristiani",
            photoUri = Uri.parse("https://cdn0.bioenciclopedia.com/es/posts/0/2/0/zorro_20_orig.jpg"),
            role = UserRole.USUARIO,
            onBack = {},
            onEditPhoto = { /* lanza galería/cámara */ },
            onOptionClick = {},
            onBecomeStudent = { /* lleva a inscripción */ },
            onLogout = {}
        )
    }
}
