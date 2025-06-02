package com.example.saborchef.viewmodel

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.*
import com.example.saborchef.ui.screens.IngredientItem
import com.example.saborchef.ui.screens.PublishRecipeData
import com.example.saborchef.ui.screens.PublishResult
import com.example.saborchef.ui.screens.StepItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

sealed class SubmitState {
    object Idle    : SubmitState()
    object Loading : SubmitState()
    object Success : SubmitState()
    data class Error(val message: String) : SubmitState()
}

class PublishRecipeViewModel(
    private val contentResolver: ContentResolver
) : ViewModel() {

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState

    private val _publishResult = MutableStateFlow(PublishResult.NONE)
    val publishResult: StateFlow<PublishResult> = _publishResult

    private val api: RecetaControllerApi by lazy {
        ApiClient(baseUrl = "http://192.168.1.37:8080/")
            .createService(RecetaControllerApi::class.java)
    }

    /**
     * Lanza todo el flujo de:
     * 1) Subida de cada foto → URL
     * 2) Armado de RecetaCrearRequest con fotos, ingredientes y pasos
     * 3) Invocación a crearReceta()
     * 4) Emisión de resultado: SUCCESS o DUPLICATE
     */
    fun publishRecipe(data: PublishRecipeData) {
        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            _publishResult.value = PublishResult.NONE

            try {
                // 1) sube todas las fotos y recoge sus URLs
                val photoUrls = data.photos.map { uri ->
                    uploadAndGetUrl(uri)
                }

                // 2) mapea ingredientes
                val ingredientesBody = data.ingredients.map { ing ->
                    IngredienteCantidad(
                        nombreIngrediente = ing.name,
                        cantidad          = ing.quantity.toDoubleOrNull() ?: 0.0,
                        unidad            = ing.unit,
                        observaciones     = null
                    )
                }

                // 3) mapea pasos (sin media aún, puedes extender si quieres contenidos multimedia)
                val pasosBody = data.steps.mapIndexed { idx, step ->
                    PasoCrear(
                        nroPaso    = idx + 1,
                        texto      = step.description,
                        contenidos = emptyList()
                    )
                }

                // 4) construye request
                val request = RecetaCrearRequest(
                    idUsuario         = 1L,  // -> reemplaza por tu user id real
                    nombreReceta      = data.name,
                    descripcionReceta = data.description,
                    fotoPrincipal     = photoUrls.firstOrNull(),
                    duracion          = data.duration,
                    porciones         = data.servings,
                    tipo              = null,  // -> tu campo de tipo si existe
                    ingredientes      = ingredientesBody,
                    pasos             = pasosBody,
                    fotos             = photoUrls.map { url ->
                        FotoCrear(
                            urlFoto     = url,
                            descripcion = "Foto receta"
                        )
                    }
                )

                // 5) llamada a crearReceta
                val resp = withContext(Dispatchers.IO) {
                    api.crearReceta(request).execute()
                }
                if (resp.isSuccessful) {
                    _submitState.value = SubmitState.Success
                    _publishResult.value = PublishResult.SUCCESS
                } else if (resp.code() == 409) {
                    // suponiendo que un 409 indica duplicado
                    _submitState.value = SubmitState.Idle
                    _publishResult.value = PublishResult.DUPLICATE
                } else {
                    val err = resp.errorBody()?.string() ?: "Código ${resp.code()}"
                    _submitState.value = SubmitState.Error("Servidor: $err")
                }

            } catch (e: Exception) {
                _submitState.value = SubmitState.Error("Error: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Sube un archivo y devuelve su URL
     */
    private suspend fun uploadAndGetUrl(uri: Uri): String = withContext(Dispatchers.IO) {
        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
        val stream   = contentResolver.openInputStream(uri)!!
        val bytes    = stream.readBytes().also { stream.close() }

        val reqBody = RequestBody.create(mimeType.toMediaTypeOrNull(), bytes)
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?: uri.lastPathSegment?.substringAfterLast('.', "") ?: "bin"

        val part = MultipartBody.Part.createFormData(
            name     = "file",
            filename = "upload_${System.currentTimeMillis()}.$extension",
            body     = reqBody
        )
        val uploadResp = api.uploadFile(part).execute()
        if (!uploadResp.isSuccessful) {
            throw RuntimeException("Upload fallo: ${uploadResp.code()}")
        }
        uploadResp.body()!!.url
    }
}
