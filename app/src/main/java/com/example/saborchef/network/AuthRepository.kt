package com.example.saborchef.network

import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes existentes
data class LoginRequest(
    val alias: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User? = null
)

data class User(
    val id: Long,
    val email: String,
    val name: String? = null
)

// Nuevas data classes para password recovery
data class PasswordResetRequest(
    val email: String
)

data class VerifyCodeRequest(
    val email: String,
    val codigo: String
)

data class NewPasswordRequest(
    val email: String,
    val nuevaPassword: String
)

data class PasswordResetResponse(
    val message: String,
    val success: Boolean
)

data class VerifyCodeResponse(val success: Boolean, val message: String)

// Interface de la API actualizada
interface AuthApiService {
    @POST("auth/authenticate")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Endpoints para password recovery
    @POST("usuarios/password/send-code")
    suspend fun sendPasswordResetEmail(@Body request: PasswordResetRequest): Response<PasswordResetResponse>

    @POST("usuarios/password/verify-code")
    suspend fun verifyResetCode(@Body request: VerifyCodeRequest): Response<VerifyCodeResponse>

    @POST("usuarios/password/reset")
    suspend fun resetPassword(@Body request: NewPasswordRequest): PasswordResetResponse
}

object AuthRepository {
    private val api: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/") // Para emulador Android
            // .baseUrl("http://localhost:8080/api/") // Para dispositivo físico en la misma red
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    // Método de login existente
    suspend fun login(alias: String, password: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(alias, password))
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Nuevos métodos para password recovery
    suspend fun sendPasswordResetEmailRaw(request: PasswordResetRequest): Response<PasswordResetResponse> {
        return api.sendPasswordResetEmail(request)
    }

    suspend fun verifyResetCode(request: VerifyCodeRequest): VerifyCodeResponse {
        return try {
            println("📤 Enviando request: email=${request.email}, code=${request.codigo}")
            val response = api.verifyResetCode(request)

            println("📥 Response code: ${response.code()}")
            println("📥 Response message: ${response.message()}")
            println("📥 Response headers: ${response.headers()}")

            if (response.isSuccessful) {
                val body = response.body()
                println("✅ Success body: $body")
                body ?: VerifyCodeResponse(false, "Respuesta vacía del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                println("❌ Error body: $errorBody")

                val message = when (response.code()) {
                    400 -> {
                        // Código incorrecto o expirado
                        try {
                            val gson = Gson()
                            val errorResponse = gson.fromJson(errorBody, VerifyCodeResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "El código ingresado es incorrecto o ha expirado"
                        }
                    }
                    403 -> {
                        // Forbidden - puede ser que el código ya expiró o hay problema de permisos
                        "El código ha expirado o no tienes permisos para esta operación. Solicita un nuevo código."
                    }
                    404 -> "No se encontró la sesión de recuperación. Solicita un nuevo código."
                    429 -> "Demasiados intentos. Espera un momento antes de intentar nuevamente."
                    500 -> "Error interno del servidor. Intenta más tarde."
                    else -> {
                        try {
                            if (!errorBody.isNullOrEmpty()) {
                                val gson = Gson()
                                val errorResponse = gson.fromJson(errorBody, VerifyCodeResponse::class.java)
                                errorResponse.message
                            } else {
                                "Error del servidor (HTTP ${response.code()})"
                            }
                        } catch (e: Exception) {
                            "Error del servidor (HTTP ${response.code()})"
                        }
                    }
                }

                println("💬 Mensaje final: $message")
                VerifyCodeResponse(false, message)
            }
        } catch (e: Exception) {
            println("💥 Excepción: ${e.javaClass.simpleName} - ${e.message}")
            e.printStackTrace()
            VerifyCodeResponse(false, "Error de conexión: ${e.message}")
        }
    }

    suspend fun resetPassword(request: NewPasswordRequest): PasswordResetResponse {
        return try {
            api.resetPassword(request)
        } catch (e: Exception) {
            PasswordResetResponse(
                message = when (e) {
                    is retrofit2.HttpException -> {
                        when (e.code()) {
                            400 -> "Datos inválidos"
                            404 -> "Sesión expirada"
                            else -> "Error del servidor"
                        }
                    }
                    is java.net.UnknownHostException -> "Error de conexión"
                    else -> "Error inesperado: ${e.message}"
                },
                success = false
            )
        }
    }
}