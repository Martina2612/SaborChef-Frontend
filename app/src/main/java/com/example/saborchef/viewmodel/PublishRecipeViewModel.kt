package com.example.saborchef.ui.publish

import android.app.Application
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saborchef.apis.RecetaControllerApi
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.data.url
import com.example.saborchef.infrastructure.ApiClient
import com.example.saborchef.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.io.path.createTempFile

data class StepItem(
    val description: String = "",
    val media: List<Uri> = emptyList()
)

class PublishRecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = DataStoreManager(application)

    private val api by lazy {
        ApiClient.createAuthenticatedClient(url).createService(RecetaControllerApi::class.java)
    }

    sealed class PublishUiState {
        object Idle : PublishUiState()
        object Loading : PublishUiState()
        object Success : PublishUiState()
        object Duplicate : PublishUiState()
        data class Error(val message: String) : PublishUiState()
    }

    private val _uiState = MutableStateFlow<PublishUiState>(PublishUiState.Idle)
    val uiState: StateFlow<PublishUiState> get() = _uiState

    // Campos para edición
    var editingRecipeId: Long? = null

    var nombreReceta = ""
    var descripcionReceta = ""
    var porcionesReceta = 1
    var duracionReceta = 1
    var tipoReceta = "OTRO"
    var fotoPrincipalBase64: String? = null
    var ingredientesList = mutableStateListOf<IngredienteCantidad>()
    var steps = mutableStateListOf(StepItem())

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

    private fun base64ToTempUri(context: Application, base64: String): Uri {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        val tempFile = createTempFile(suffix = ".jpg").toFile()
        tempFile.writeBytes(bytes)
        return Uri.fromFile(tempFile)
    }

    fun updateStepDescription(index: Int, text: String) {
        steps[index] = steps[index].copy(description = text)
    }

    fun updateStepMedia(index: Int, uris: List<Uri>) {
        steps[index] = steps[index].copy(media = uris)
    }

    /**
     * Método para crear receta nueva (POST)
     */
    private fun createRecipe(
        photos: List<Uri>,
        nombre: String,
        descripcion: String,
        duracion: Int,
        porciones: Int,
        tipo: String,
        ingredientes: List<IngredienteCantidad>,
        pasos: List<StepItem>
    ) {
        viewModelScope.launch {
            val userId = dataStore.userId.firstOrNull() ?: 0L
            _uiState.value = PublishUiState.Loading
            try {
                val fotoPrincipal = photos.firstOrNull()?.let { uriToBase64(it) }
                val fotosDto = photos.mapNotNull { uri ->
                    uriToBase64(uri)?.let { base64 ->
                        FotoCrear(urlFoto = base64, descripcion = null)
                    }
                }
                val pasosDto = pasos.mapIndexed { idx, step ->
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
                    tipo = tipo.uppercase(),
                    ingredientes = ingredientes,
                    pasos = pasosDto,
                    fotos = fotosDto
                )

                val response = withContext(Dispatchers.IO) {
                    api.crearReceta(request).execute()
                }
                if (response.isSuccessful) {
                    _uiState.value = PublishUiState.Success
                } else if (response.code() == 409 || response.code() == 403) {
                    // Manejo de duplicados si quieres usarlo
                    _uiState.value = PublishUiState.Duplicate
                } else {
                    _uiState.value = PublishUiState.Error("Error ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PublishVM", "Error publishing", e)
                _uiState.value = PublishUiState.Error(e.localizedMessage ?: "Unexpected error")
            }
        }
    }

    /**
     * Método para actualizar receta existente (PUT)
     */
    fun updateRecipe(
        recipeId: Long,
        photos: List<Uri>,
        nombre: String,
        descripcion: String,
        duracion: Int,
        porciones: Int,
        tipo: String,
        ingredientes: List<IngredienteCantidad>,
        pasos: List<StepItem>
    ) {
        viewModelScope.launch {
            _uiState.value = PublishUiState.Loading
            try {
                val fotoPrincipal = photos.firstOrNull()?.let { uriToBase64(it) }
                val fotosDto = photos.mapNotNull { uri ->
                    uriToBase64(uri)?.let { base64 ->
                        FotoCrear(urlFoto = base64, descripcion = null)
                    }
                }
                val pasosDto = pasos.mapIndexed { idx, step ->
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
                    idUsuario = dataStore.userId.firstOrNull() ?: 0L,
                    nombreReceta = nombre,
                    descripcionReceta = descripcion,
                    fotoPrincipal = fotoPrincipal,
                    duracion = duracion,
                    porciones = porciones,
                    tipo = tipo.uppercase(),
                    ingredientes = ingredientes,
                    pasos = pasosDto,
                    fotos = fotosDto
                )

                val response = withContext(Dispatchers.IO) {
                    api.actualizar(recipeId, request).execute()
                }

                if (response.isSuccessful) {
                    _uiState.value = PublishUiState.Success
                } else {
                    _uiState.value = PublishUiState.Error("Error actualizando receta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PublishVM", "Error updating recipe", e)
                _uiState.value = PublishUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }

    /**
     * Método público para crear o actualizar según el estado editingRecipeId
     */
    fun submitRecipe(
        photos: List<Uri>,
        nombre: String,
        descripcion: String,
        duracion: Int,
        porciones: Int,
        tipo: String,
        ingredientes: List<IngredienteCantidad>,
        pasos: List<StepItem>
    ) {
        val recipeId = editingRecipeId
        if (recipeId == null) {
            createRecipe(photos, nombre, descripcion, duracion, porciones, tipo, ingredientes, pasos)
        } else {
            updateRecipe(recipeId, photos, nombre, descripcion, duracion, porciones, tipo, ingredientes, pasos)
        }
    }

    /**
     * Cargar receta por ID para edición
     */
    fun loadRecipeById(recipeId: Long) {
        editingRecipeId = recipeId
        viewModelScope.launch {
            _uiState.value = PublishUiState.Loading
            try {
                val response = withContext(Dispatchers.IO) {
                    api.obtener(recipeId).execute()
                }

                if (response.isSuccessful) {
                    val receta = response.body()
                    receta?.let {
                        nombreReceta = it.nombre.orEmpty()
                        descripcionReceta = it.descripcion.orEmpty()
                        porcionesReceta = it.porciones ?: 1
                        duracionReceta = it.duracion ?: 1
                        tipoReceta = it.tipo.orEmpty()
                        fotoPrincipalBase64 = it.fotoPrincipal

                        ingredientesList.clear()
                        ingredientesList.addAll(
                            it.ingredientes.orEmpty().map { ing ->
                                IngredienteCantidad(
                                    nombreIngrediente = ing.nombre.toString(),
                                    cantidad = (ing.cantidad ?: 0.0).toFloat(),
                                    unidad = ing.unidad.orEmpty(),
                                    observaciones = ing.observaciones
                                )
                            }
                        )

                        steps.clear()
                        steps.addAll(
                            it.pasos.orEmpty().map { paso ->
                                StepItem(
                                    description = paso.texto.orEmpty(),
                                    media = paso.contenidos.orEmpty().mapNotNull { contenido ->
                                        contenido.url?.let { base64 ->
                                            base64ToTempUri(getApplication(), base64)
                                        }
                                    }
                                )
                            }
                        )

                        _uiState.value = PublishUiState.Idle
                    }
                } else {
                    _uiState.value = PublishUiState.Error("Error al cargar la receta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("PublishVM", "Error loading recipe", e)
                _uiState.value = PublishUiState.Error(e.localizedMessage ?: "Error inesperado")
            }
        }
    }

    fun resetState() {
        _uiState.value = PublishUiState.Idle
    }
}
