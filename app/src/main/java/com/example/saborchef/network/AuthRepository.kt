package com.example.saborchef.network

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.saborchef.model.ConfirmacionCodigoDTO
import com.example.saborchef.model.RegisterRequest
import com.example.saborchef.models.AuthenticationResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.saborchef.network.LoginRequest

// Data classes (pueden ir en archivos separados si preferís)

data class LoginResponse(val token: String, val user: User? = null)
data class User(val id: Long, val email: String, val name: String? = null)

data class PasswordResetRequest(val email: String)
data class VerifyCodeRequest(val email: String, val codigo: String)
data class NewPasswordRequest(val email: String, val nuevaPassword: String)

data class PasswordResetResponse(val message: String, val success: Boolean)
data class VerifyCodeResponse(val success: Boolean, val message: String)

data class ConvertToStudentRequest(
    val userId: Long,
    val numeroTarjeta: String,
    val dniFrente: String, // En producción sería la URL del archivo subido
    val dniDorso: String,  // En producción sería la URL del archivo subido
    val cuentaCorriente: String
)
data class ConvertToStudentResponse(
    val success: Boolean,
    val message: String,
    val alumno: StudentData? = null
)
data class StudentData(
    val idAlumno: Long,
    val numeroTarjeta: String,
    val dniFrente: String,
    val dniDorso: String,
    val cuentaCorriente: String
)

object AuthRepository {

    private val api: AuthApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        Retrofit.Builder()
            .baseUrl("http://192.168.1.37:8080/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(AuthApiService::class.java)
    }

    suspend fun registerUser(request: RegisterRequest): Result<AuthenticationResponse> {
        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("El cuerpo de la respuesta es null"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en registerUser", e)
            Result.failure(e)
        }
    }

    suspend fun confirmarCuenta(dto: ConfirmacionCodigoDTO): Result<Unit> {
        return try {
            val response = api.confirmarCuenta(dto)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en confirmarCuenta", e)
            Result.failure(e)
        }
    }

    suspend fun login(alias: String, password: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(alias, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && !body.accessToken.isNullOrEmpty()) {
                    Result.success(body.accessToken!!)
                } else {
                    Result.failure(Exception("El accessToken viene nulo o vacío"))
                }
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en login", e)
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmailRaw(request: PasswordResetRequest): Response<PasswordResetResponse> {
        return api.sendPasswordResetEmail(request)
    }

    suspend fun verifyResetCode(request: VerifyCodeRequest): VerifyCodeResponse {
        return try {
            val response = api.verifyResetCode(request)
            if (response.isSuccessful) {
                response.body() ?: VerifyCodeResponse(false, "Respuesta vacía del servidor")
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> Gson().fromJson(errorBody, VerifyCodeResponse::class.java)?.message ?: "Código inválido"
                    403 -> "Código expirado o sin permisos"
                    404 -> "No se encontró la sesión"
                    429 -> "Demasiados intentos"
                    500 -> "Error interno del servidor"
                    else -> "Error del servidor (${response.code()})"
                }
                VerifyCodeResponse(false, message)
            }
        } catch (e: Exception) {
            VerifyCodeResponse(false, "Error de conexión: ${e.message}")
        }
    }

    suspend fun resetPassword(request: NewPasswordRequest): PasswordResetResponse {
        return try {
            api.resetPassword(request)
        } catch (e: Exception) {
            PasswordResetResponse(
                message = when (e) {
                    is retrofit2.HttpException -> when (e.code()) {
                        400 -> "Datos inválidos"
                        404 -> "Sesión expirada"
                        else -> "Error del servidor"
                    }
                    is java.net.UnknownHostException -> "Error de conexión"
                    else -> "Error inesperado: ${e.message}"
                },
                success = false
            )
        }
    }

    suspend fun convertToStudent(request: ConvertToStudentRequest): Result<ConvertToStudentResponse> {
        return try {
            val response = api.convertToStudent(request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("El cuerpo de la respuesta es null"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en convertToStudent", e)
            Result.failure(e)
        }
    }

    // Método helper para subir archivos (DNI)
    suspend fun uploadDniFiles(
        frontUri: Uri,
        backUri: Uri,
        context: Context
    ): Pair<String, String> {
        // Aquí implementarías la lógica para subir archivos
        // Por ahora retorno URLs simuladas
        return Pair(
            "https://storage.example.com/dni/front_${System.currentTimeMillis()}.jpg",
            "https://storage.example.com/dni/back_${System.currentTimeMillis()}.jpg"
        )
    }
}
