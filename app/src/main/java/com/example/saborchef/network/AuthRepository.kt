package com.example.saborchef.network

import android.util.Log
import com.example.saborchef.model.ConfirmacionCodigoDTO
import com.example.saborchef.model.RegisterRequest
import com.example.saborchef.models.AuthenticationResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que expone:
 *   - registerUser(...)        → Result<AuthenticationResponse>
 *   - confirmarCuenta(...)     → Result<Unit>
 *   - login(...)               → Result<String> (token)
 *
 * Revisa que la baseUrl termine en “/api/”.
 */
object AuthRepository {

    // 1) Creamos retrofit + AuthApiService con interceptor de logging para ver peticiones
    private val api: AuthApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/api/")   // Asegúrate de que coincide con tu configuración real
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(AuthApiService::class.java)
    }

    /**
     * Registra usuario en el backend. Retorna Result<AuthenticationResponse> con el token, userId, role, email.
     */
    suspend fun registerUser(request: RegisterRequest): Result<AuthenticationResponse> {
        return try {
            val response: Response<AuthenticationResponse> = api.register(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("El cuerpo de la respuesta es null"))
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en registerUser", e)
            Result.failure(e)
        }
    }

    /**
     * Envía el DTO de confirmación de código al backend. Retorna Result<Unit>.
     */
    suspend fun confirmarCuenta(dto: ConfirmacionCodigoDTO): Result<Unit> {
        return try {
            val response: Response<Void> = api.confirmarCuenta(dto)
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

    /**
     * Hace login contra el endpoint “auth/authenticate”.
     * Si es exitoso, extrae body.accessToken y lo devuelve en Result.success(token).
     */
    suspend fun login(alias: String, password: String): Result<String> {
        return try {
            val response: Response<AuthenticationResponse> = api.login(LoginRequest(alias, password))
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
}
