package com.example.saborchef.network

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.saborchef.model.PerfilUsuarioDTO
import com.example.saborchef.models.AlumnoActualizarDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object UsuarioRepository {

    private val api: UsuarioApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        Retrofit.Builder()
            .baseUrl("https://saborchef-backend-production.up.railway.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UsuarioApiService::class.java)
    }

    /**
     * Obtiene el perfil completo de un usuario
     */
    suspend fun obtenerPerfil(userId: Long): Result<PerfilUsuarioDTO> {
        return try {
            val response = api.obtenerPerfil(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("El cuerpo de la respuesta es null"))
            } else {
                Log.e("UsuarioRepository", "Error HTTP ${response.code()}: ${response.message()}")
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error en obtenerPerfil", e)
            Result.failure(e)
        }
    }

    suspend fun convertirEnAlumno(userId: String, alumnoDto: AlumnoActualizarDTO, token: String): Result<String> {
        return try {
            // CORRECCIÓN: Usar 'api' en lugar de 'UsuarioapiService'
            val response = api.convertirEnAlumno(
                userId = userId.toInt(),
                alumnoDto = alumnoDto,
                authorization = "Bearer $token"
            )

            if (response.isSuccessful) {
                Log.d("UsuarioRepository", "✅ Usuario convertido a alumno exitosamente")
                Result.success("Usuario convertido a alumno exitosamente")
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Log.e("UsuarioRepository", "❌ Error al convertir: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al convertir usuario: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "❌ Excepción en convertirEnAlumno", e)
            Result.failure(e)
        }
    }
    /**
     * Actualiza el perfil de un usuario
     */
    suspend fun actualizarPerfil(userId: Long, perfil: PerfilUsuarioDTO): Result<PerfilUsuarioDTO> {
        return try {
            val response = api.actualizarPerfil(userId, perfil)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("El cuerpo de la respuesta es null"))
            } else {
                Log.e("UsuarioRepository", "Error HTTP ${response.code()}: ${response.message()}")
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "Error en actualizarPerfil", e)
            Result.failure(e)
        }
    }

    /**
     * Sube una foto de perfil CON TOKEN
     */
    suspend fun subirFotoPerfil(context: Context, userId: Long, imageUri: Uri, token: String): Result<String> {
        return try {
            Log.d("UsuarioRepository", "📤 Subiendo imagen desde Uri: $imageUri")

            // 1. Crear archivo temporal desde URI
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri) ?: return Result.failure(Exception("No se pudo abrir el archivo"))
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            tempFile.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }

            // 2. Convertir a RequestBody + Multipart
            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("foto", tempFile.name, requestFile)

            // 3. Enviar con token
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.subirFotoPerfil(userId, body, authToken)

            // 4. Procesar respuesta
            if (response.isSuccessful) {
                val fotoUrl = response.body()?.get("fotoUrl") ?: ""
                Log.d("UsuarioRepository", "✅ Foto subida exitosamente: $fotoUrl")
                Result.success(fotoUrl)
            } else {
                Log.e("UsuarioRepository", "❌ Error HTTP ${response.code()}: ${response.message()}")
                Log.e("UsuarioRepository", "❌ Body: ${response.errorBody()?.string()}")
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "❌ Error en subirFotoPerfil", e)
            Result.failure(e)
        }
    }

}