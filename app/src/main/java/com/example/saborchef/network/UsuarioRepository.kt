package com.example.saborchef.network

import android.util.Log
import com.example.saborchef.model.PerfilUsuarioDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    suspend fun subirFotoPerfil(userId: Long, imageBase64: String, token: String): Result<String> {
        return try {
            Log.d("UsuarioRepository", "Subiendo foto para usuario $userId")

            // Convertir Base64 a ByteArray
            val imageBytes = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT)

            // Crear MultipartBody.Part
            val requestFile = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("foto", "profile.jpg", requestFile)

            // Agregar Bearer al token
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

            val response = api.subirFotoPerfil(userId, body, authToken)
            if (response.isSuccessful) {
                val fotoUrl = response.body()?.get("fotoUrl") ?: ""
                Log.d("UsuarioRepository", "✅ Foto subida exitosamente: $fotoUrl")
                Result.success(fotoUrl)
            } else {
                Log.e("UsuarioRepository", "❌ Error HTTP ${response.code()}: ${response.message()}")
                Log.e("UsuarioRepository", "❌ Error body: ${response.errorBody()?.string()}")
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("UsuarioRepository", "❌ Error en subirFotoPerfil", e)
            Result.failure(e)
        }
    }
}