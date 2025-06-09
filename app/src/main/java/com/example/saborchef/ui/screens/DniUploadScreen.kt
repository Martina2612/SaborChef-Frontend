package com.example.saborchef.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.R
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.components.FileSlot
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins

@Composable
fun DniUploadScreen(
    onBack: () -> Unit,
    onFinish: (frontUri: Uri, backUri: Uri, tramite: String) -> Unit
) {
    val context = LocalContext.current

    var frontUri by remember { mutableStateOf<Uri?>(null) }
    var backUri  by remember { mutableStateOf<Uri?>(null) }
    var tramite  by remember { mutableStateOf("") }
    var tramiteError by remember { mutableStateOf<String?>(null) }
    var imageError by remember { mutableStateOf<String?>(null) }

    val pickFront = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        frontUri = it
        imageError = null
    }
    val pickBack = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        backUri = it
        imageError = null
    }

    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .size(36.dp)
                .background(Color.White, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                tint = BlueDark
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.chef_dni),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))
            Text(
                "¡Último paso!",
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = BlueDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "Subí tu DNI frente y dorso",
                fontFamily = Poppins,
                color = BlueDark.copy(alpha = 0.7f),
                style = MaterialTheme.typography.body2
            )

            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                FileSlot(
                    uri = frontUri,
                    placeholder = "DNI Frente",
                    onClickPlaceholder = { pickFront.launch("image/*") },
                    onRemove = { frontUri = null }
                )
                FileSlot(
                    uri = backUri,
                    placeholder = "DNI Dorso",
                    onClickPlaceholder = { pickBack.launch("image/*") },
                    onRemove = { backUri = null }
                )
            }
            // Error si falta alguna imagen
            imageError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "Número de trámite",
                fontFamily = Poppins,
                color = BlueDark.copy(alpha = 0.7f),
                style = MaterialTheme.typography.body2
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = tramite,
                onValueChange = {
                    if (it.length <= 11 && it.all { ch -> ch.isDigit() }) {
                        tramite = it
                        tramiteError = null
                    }
                },
                placeholder = { Text("00000000000", fontFamily = Poppins) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = tramiteError != null,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (tramiteError != null) Color.Red else BlueDark.copy(alpha = 0.6f),
                    unfocusedBorderColor = if (tramiteError != null) Color.Red else BlueDark.copy(alpha = 0.3f),
                    cursorColor = OrangeDark,
                    placeholderColor = BlueDark.copy(alpha = 0.4f),
                    textColor = BlueDark
                )
            )
            tramiteError?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(32.dp))
            AppButton(
                text = "Finalizar",
                onClick = {
                    // Validaciones
                    when {
                        frontUri == null || backUri == null -> {
                            imageError = "Debes subir las dos imágenes"
                        }
                        tramite.length < 11 -> {
                            tramiteError = "El número debe tener 11 dígitos"
                        }
                        else -> {
                            // Ambos URIs no null y trámite OK
                            onFinish(frontUri!!, backUri!!, tramite)
                        }
                    }
                },
                primary = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}



// Utilidad para convertir imagen Uri a base64
fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }
}


