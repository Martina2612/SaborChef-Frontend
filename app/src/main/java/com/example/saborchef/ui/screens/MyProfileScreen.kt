package com.example.saborchef.ui.screens

import android.net.Uri
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.saborchef.ui.components.CurvedHeader
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.ProfilePhotoSelector
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.SaborChefTheme
import com.example.saborchef.ui.theme.Poppins
import coil.compose.AsyncImage
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.ui.theme.BlueLight
import com.example.saborchef.network.UsuarioRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.example.saborchef.model.Rol
import androidx.compose.material3.*

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
    println("🔵 ProfileScreen iniciado")

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Estados para manejar la foto
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingPhoto by remember { mutableStateOf(false) }

    println("🔵 Estados inicializados")

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // 1) Header curvo
            CurvedHeader(
                title = "",
                icon = Icons.Default.ArrowBack,
                headerColor = OrangeDark,
                circleColor = OrangeDark,
                onBack = onBack,
                height = 140.dp
            )

            // 2) Avatar con selector de foto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-160).dp),
                contentAlignment = Alignment.TopCenter
            ) {
                ProfilePhotoSelector(
                    currentPhotoUrl = null, // Por ahora null, después cargaremos del usuario
                    currentPhotoUri = selectedPhotoUri ?: photoUri,
                    onPhotoSelected = { uri ->
                        println("🟡 INICIO - Foto seleccionada: $uri")
                        println("🟡 selectedPhotoUri antes: $selectedPhotoUri")
                        selectedPhotoUri = uri
                        println("🟡 selectedPhotoUri después: $selectedPhotoUri")
                        isUploadingPhoto = true
                        println("🟡 isUploadingPhoto = true")

                        println("🟡 Antes del coroutineScope.launch")

                        coroutineScope.launch {
                            println("🟢 DENTRO del coroutineScope.launch")

                            try {
                                println("🟢 Intentando obtener datos del DataStore...")

                                // Obtener userId Y TOKEN del DataStore
                                val userId = dataStoreManager.userId.firstOrNull() ?: 1L
                                val token = dataStoreManager.token.firstOrNull() ?: ""

                                println("🔍 Debug - userId: $userId")
                                println("🔍 Token completo: '$token'")
                                println("🔍 Token length: ${token.length}")
                                println("🔍 Token está vacío: ${token.isEmpty()}")

                                println("🟢 Iniciando conversión a Base64...")

                                // Convertir URI a Base64
                                val base64 = try {
                                    val inputStream = context.contentResolver.openInputStream(uri)
                                    val bytes = inputStream?.readBytes()
                                    inputStream?.close()
                                    bytes?.let { android.util.Base64.encodeToString(it, android.util.Base64.NO_WRAP) }
                                } catch (e: Exception) {
                                    println("❌ Error convirtiendo imagen: ${e.message}")
                                    null
                                }

                                println("🔍 Base64 generado: ${if (base64 != null) "SÍ (${base64.length} chars)" else "NO"}")

                                if (base64 != null && token.isNotEmpty()) {
                                    println("📤 TODO LISTO - Subiendo foto al servidor...")
                                    println("📤 UserId: $userId")
                                    println("📤 Token preview: ${token.take(50)}...")

                                    // Subir al servidor CON TOKEN
                                    UsuarioRepository.subirFotoPerfil(userId, base64, token)
                                        .onSuccess { fotoUrl ->
                                            println("✅ Foto subida exitosamente: $fotoUrl")
                                            isUploadingPhoto = false
                                        }
                                        .onFailure { e ->
                                            println("❌ Error subiendo foto: ${e.message}")
                                            println("❌ Error type: ${e.javaClass.simpleName}")
                                            isUploadingPhoto = false
                                        }
                                } else {
                                    println("❌ NO SE PUEDE SUBIR:")
                                    println("   - Token vacío: ${token.isEmpty()}")
                                    println("   - Base64 null: ${base64 == null}")
                                    isUploadingPhoto = false
                                }
                            } catch (e: Exception) {
                                println("🔴 ERROR GENERAL: ${e.message}")
                                println("🔴 Stack trace: ${e.stackTrace.joinToString("\n")}")
                                isUploadingPhoto = false
                            }
                        }

                        println("🟡 Después del coroutineScope.launch")
                    }
                )

                // Indicador de carga mientras sube la foto
                if (isUploadingPhoto) {
                    Box(
                        modifier = Modifier
                            .size(95.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
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

            // 4) Opciones
            val opciones = remember(role) {
                mutableListOf<ProfileOption>().apply {
                    add(ProfileOption(Icons.Default.Person, "Mis datos") { onOptionClick("Mis datos") })
                    add(ProfileOption(Icons.Default.Book, "Mis recetas") { onOptionClick("Mis recetas") })
                    if (role == Rol.ALUMNO) {
                        add(ProfileOption(Icons.Default.OndemandVideo, "Mis cursos") { onOptionClick("Mis cursos") })
                        add(ProfileOption(Icons.Default.Payment, "Medio de pago") { onOptionClick("Medios de pago") })
                    }
                    add(ProfileOption(Icons.Default.OutlinedFlag, "Términos y condiciones") { onOptionClick("Términos y condiciones") })
                    add(ProfileOption(Icons.Default.Phone, "Contáctanos") { onOptionClick("Contáctanos") })
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-130).dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                opciones.forEach { opt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                            .shadow(elevation = 1.dp, shape = RoundedCornerShape(8.dp))
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

            // 5) Botón "¡Hazte alumno!" solo para USUARIO
            if (role == Rol.USUARIO) {
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

            // BOTÓN TEMPORAL - CERRAR SESIÓN
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        println("🔴 Limpiando datos...")
                        dataStoreManager.clearUserData()
                        println("🔴 Datos limpiados, navegando a welcome")
                        navController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Text("CERRAR SESIÓN (Temporal)", color = Color.White)
            }
        }
    }
}