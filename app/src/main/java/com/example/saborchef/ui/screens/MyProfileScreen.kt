package com.example.saborchef.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.Rol
import com.example.saborchef.network.UsuarioRepository
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.components.ProfilePhotoSelector
import com.example.saborchef.ui.theme.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ProfileOption(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun ProfileScreen(
    userName: String,
    photoUri: Uri?,
    role: Rol,
    onBack: () -> Unit,
    onEditPhoto: () -> Unit,
    onOptionClick: (label: String) -> Unit,
    onBecomeStudent: () -> Unit,
    onLogout: () -> Unit,
    dataStoreManager: DataStoreManager,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var currentPhotoUrl by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            CurvedHeader(
                title = "",
                icon = Icons.Default.ArrowBack,
                headerColor = OrangeDark,
                circleColor = OrangeDark,
                onBack = onBack,
                height = 140.dp
            )

            Box(
                modifier = Modifier.fillMaxWidth().offset(y = (-160).dp),
                contentAlignment = Alignment.TopCenter
            ) {
                ProfilePhotoSelector(
                    currentPhotoUrl = currentPhotoUrl,
                    currentPhotoUri = selectedPhotoUri ?: photoUri,
                    onPhotoSelected = { uri ->
                        println("📸 Foto seleccionada: $uri")
                        selectedPhotoUri = uri
                        isUploadingPhoto = true

                        coroutineScope.launch {
                            try {
                                val userId = dataStoreManager.userId.firstOrNull()
                                val token = dataStoreManager.token.firstOrNull()

                                println("🔍 userId: $userId")
                                println("🔍 token: ${token?.take(20)}... (${token?.length ?: 0} caracteres)")

                                if (userId == null || token.isNullOrBlank()) {
                                    println("❌ userId o token no disponible. Abortando subida.")
                                    Toast.makeText(context, "Error al subir: sesión inválida", Toast.LENGTH_SHORT).show()
                                    isUploadingPhoto = false
                                    return@launch
                                }

                                println("🚀 Iniciando subida de imagen...")
                                val result = UsuarioRepository.subirFotoPerfil(
                                    context = context,
                                    userId = userId,
                                    imageUri = uri,
                                    token = token
                                )

                                result.onSuccess { fotoUrl ->
                                    println("✅ Imagen subida con éxito. URL: $fotoUrl")
                                    currentPhotoUrl = fotoUrl
                                    Toast.makeText(context, "Foto actualizada exitosamente", Toast.LENGTH_SHORT).show()
                                }.onFailure { e ->
                                    println("❌ Fallo al subir la imagen: ${e.message}")
                                    e.printStackTrace()
                                    Toast.makeText(context, "Error al subir la foto", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                println("🟥 Excepción inesperada al subir foto: ${e.message}")
                                e.printStackTrace()
                                Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show()
                            } finally {
                                isUploadingPhoto = false
                            }
                        }
                    }
                )
                if (isUploadingPhoto) {
                    Box(
                        modifier = Modifier.size(95.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Text(
                text = userName,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = BlueDark,
                modifier = Modifier.align(Alignment.CenterHorizontally).offset(y = (-150).dp)
            )

            val opciones = remember(role) {
                mutableListOf(
                    ProfileOption(Icons.Default.Person, "Mis datos") { onOptionClick("Mis datos") },
                    ProfileOption(Icons.Default.Book, "Mis recetas") { onOptionClick("Mis recetas") },
                ).apply {
                    if (role == Rol.ALUMNO) {
                        add(ProfileOption(Icons.Default.OndemandVideo, "Mis cursos") { onOptionClick("Mis cursos") })
                        add(ProfileOption(Icons.Default.Payment, "Medio de pago") { onOptionClick("Medios de pago") })
                    }
                    add(ProfileOption(Icons.Default.OutlinedFlag, "Términos y condiciones") { onOptionClick("Términos y condiciones") })
                    add(ProfileOption(Icons.Default.Phone, "Contáctanos") { onOptionClick("Contáctanos") })
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).offset(y = (-130).dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                opciones.forEach { opt ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .shadow(1.dp, RoundedCornerShape(8.dp))
                            .clickable { opt.onClick() }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = opt.icon, contentDescription = opt.label, tint = BlueDark)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = opt.label, fontFamily = Poppins, color = BlueDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (role == Rol.USUARIO) {
                AppButton(
                    text = "¡Hazte alumno!",
                    onClick = onBecomeStudent,
                    primary = true,
                    modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.clearUserData()
                        navController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 24.dp)
            ) {
                Text("CERRAR SESIÓN (Temporal)", color = Color.White)
            }
        }
    }
}
