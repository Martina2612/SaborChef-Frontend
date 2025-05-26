package com.example.saborchef.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saborchef.R
import com.example.saborchef.ui.components.AppButton
import com.example.saborchef.ui.theme.BlueDark
import com.example.saborchef.ui.theme.OrangeDark
import com.example.saborchef.ui.theme.Poppins
import com.example.saborchef.ui.components.FileSlot
import com.example.saborchef.ui.theme.SaborChefTheme

@Composable
fun DniUploadScreen(
    onBack: () -> Unit,
    onFinish: (frontUri: Uri?, backUri: Uri?, tramite: String) -> Unit
) {
    // Estados
    var frontUri by remember { mutableStateOf<Uri?>(null) }
    var backUri  by remember { mutableStateOf<Uri?>(null) }
    var tramite  by remember { mutableStateOf("") }

    // Launchers para seleccionar imagen o PDF
    val pickFront = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) frontUri = it
    }
    val pickBack = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) backUri = it
    }

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

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(Modifier.height(60.dp))
        Image(
            painter = painterResource(R.drawable.chef_dni),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(24.dp))

        // Título
        Text(
            "¡Último paso!",
            fontFamily = Poppins,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            color = BlueDark,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Por favor, verifica tu identidad",
                fontFamily = Poppins,
                color = BlueDark,
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Subí una foto de tu DNI de frente y dorso",
                fontFamily = Poppins,
                color = BlueDark.copy(alpha = 0.7f),
                style = MaterialTheme.typography.body2
            )
            Spacer(Modifier.height(16.dp))

            // Fila de dos slots
            Row(
                horizontalArrangement = Arrangement.Absolute.spacedBy(50.dp,Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) {
                FileSlot(
                    uri = frontUri,
                    placeholder = "Seleccionar archivos",
                    onClickPlaceholder = { pickFront.launch("*/*") },
                    onRemove = { frontUri = null }
                )
                FileSlot(
                    uri = backUri,
                    placeholder = "Seleccionar archivos",
                    onClickPlaceholder = { pickBack.launch("*/*") },
                    onRemove = { backUri = null }
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Ingresá el número de trámite para validarlo",
                fontFamily = Poppins,
                color = BlueDark.copy(alpha = 0.7f),
                style = MaterialTheme.typography.body2,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(15.dp))

            OutlinedTextField(
                value = tramite,
                onValueChange = { tramite = it },
                placeholder = { Text("00000000000", fontFamily = Poppins) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = BlueDark.copy(alpha = 0.6f),
                    unfocusedBorderColor = BlueDark.copy(alpha = 0.3f),
                    cursorColor          = OrangeDark,
                    placeholderColor     = BlueDark.copy(alpha = 0.4f),
                    textColor            = BlueDark
                )
            )

            Spacer(Modifier.height(32.dp))

            AppButton(
                text = "Finalizar",
                onClick = { onFinish(frontUri, backUri, tramite) },
                primary = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DniUploadScreenPreview() {
    val fakeFront = remember { Uri.parse("content://dni/front.jpg") }
    val fakeBack = remember { Uri.parse("content://dni/back.jpg") }
    val tramiteExample = remember { "Alta de usuario" }

    SaborChefTheme {
        DniUploadScreen(
            onBack = { /* Acción de retroceso simulada */ },
            onFinish = { frontUri, backUri, tramite ->
                // Simular acción de finalizar
                println("DNI frontal: $frontUri")
                println("DNI trasero: $backUri")
                println("Trámite: $tramite")
            }
        )
    }
}

