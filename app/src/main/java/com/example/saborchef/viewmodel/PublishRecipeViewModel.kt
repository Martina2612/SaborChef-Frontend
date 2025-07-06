package com.example.saborchef.ui.publish

import android.app.Application
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.url
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.*
import com.example.saborchef.ui.screens.uriToBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// StepItem top-level

data class StepItem(
    val description: String = "",
    val media: List<Uri> = emptyList()
)

class PublishRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStoreManager(application)
    private val api by lazy {
        ApiClient
            .createAuthenticatedClient(url)
            .createService(RecetaControllerApi::class.java)
    }

    sealed class PublishUiState {
        object Idle : PublishUiState()
        object Loading : PublishUiState()
        object Success : PublishUiState()
        object Duplicate : PublishUiState()
        data class Error(val message: String) : PublishUiState()
    }

    private val _uiState = MutableStateFlow<PublishUiState>(PublishUiState.Idle)
    val uiState: StateFlow<PublishUiState>
        get() = _uiState

    var steps = mutableStateListOf(StepItem())

    private var lastRequest: RecetaCrearRequest? = null
    private var lastExistingRecipeId: Long? = null

    private fun uriToBase64(uri: Uri?): String? {
        return try {
            uri?.let {
                val inputStream = getApplication<Application>().contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                bytes?.let { b -> Base64.encodeToString(b, Base64.NO_WRAP) }
            }
        } catch (e: Exception) {
            Log.e("PublishVM", "Error converting URI to Base64", e)
            null
        }
    }

    fun updateStepDescription(index: Int, text: String) {
        steps[index] = steps[index].copy(description = text)
    }

    fun updateStepMedia(index: Int, uris: List<Uri>) {
        steps[index] = steps[index].copy(media = uris)
    }

    fun submitRecipe(
        photos: List<Uri>,
        nombre: String,
        descripcion: String,
        duracion: Int,
        porciones: Int,
        tipo: String,
        ingredientes: List<IngredienteCantidad>
    ) {
        viewModelScope.launch {
            val userId = dataStore.userId.firstOrNull() ?: 0L
            _uiState.value = PublishUiState.Loading
            try {
                val fotoPrincipal = photos.firstOrNull()?.let { uriToBase64(it) }
                val fotosDto = photos.mapNotNull { uri ->
                    uriToBase64(uri)?.let { FotoCrear(urlFoto = it, descripcion = null) }
                }
                val pasosDto = steps.mapIndexed { idx, step ->
                    val contenidos = step.media.mapNotNull { uri ->
                        uriToBase64(uri)?.let { base64 ->
                            val extension = uri.lastPathSegment?.substringAfterLast('.') ?: ""
                            MultimediaCrear(
                                tipoContenido = if (uri.toString().endsWith(".mp4")) "video" else "image",
                                extension = extension,
                                urlContenido = base64
                            )
                        }
                    }
                    PasoCrear(nroPaso = idx + 1, texto = step.description, contenidos = contenidos)
                }

                val request = RecetaCrearRequest(
                    idUsuario = userId,
                    nombreReceta = nombre,
                    descripcionReceta = descripcion,
                    fotoPrincipal = fotoPrincipal,
                    duracion = duracion,
                    porciones = porciones,
                    tipo = tipo.toUpperCase(),
                    ingredientes = ingredientes,
                    pasos = pasosDto,
                    fotos = fotosDto
                )
                lastRequest = request

                val response = withContext(Dispatchers.IO) {
                    api.crearReceta(request).execute()
                }
                when {
                    response.isSuccessful -> {
                        _uiState.value = PublishUiState.Success
                    }
                    response.code() == 409 || response.code() == 403 -> {
                        lastExistingRecipeId = response.headers()["X-Existing-Recipe-Id"]?.toLongOrNull()
                        Log.d("PublishVM", "Duplicate! existingId = $lastExistingRecipeId")
                        _uiState.value = PublishUiState.Duplicate
                    }
                    else -> {
                        _uiState.value = PublishUiState.Error("Error ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("PublishVM", "Error publishing", e)
                _uiState.value = PublishUiState.Error(e.localizedMessage ?: "Unexpected error")
            }
        }
    }

    /**
     * Llamado cuando el usuario confirma reemplazo en el diálogo
     */
    fun confirmReplace() {
        val req = lastRequest
        val recetaId = lastExistingRecipeId
        if (req == null || recetaId == null) {
            Log.e("PublishVM", "confirmReplace fue llamado con req=$req, recetaId=$recetaId")
            return
        }

        viewModelScope.launch {
            _uiState.value = PublishUiState.Loading
            try {
                Log.d("PublishVM", "Calling PUT api/recetas/$recetaId …")
                // Llamada a actualizar receta
                val response = withContext(Dispatchers.IO) {
                    api.actualizar(recetaId, req).execute()
                }
                Log.d("PublishVM", "update response code=${response.code()}, body=${response.errorBody()?.string()}")
                _uiState.value = if (response.isSuccessful) {
                    PublishUiState.Success
                } else {
                    PublishUiState.Error("Error al reemplazar: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PublishVM", "Error replacing recipe", e)
                _uiState.value = PublishUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }


    fun resetState() {
        _uiState.value = PublishUiState.Idle
    }


}