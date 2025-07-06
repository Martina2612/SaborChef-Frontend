package com.example.saborchef.network

import android.content.Context
import android.util.Log
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.Clase
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.flow.first

object ClaseRepository {
    private val api: ClaseApi = Retrofit.Builder()
        .baseUrl("https://saborchef-backend-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClaseApi::class.java)

    // MÉTODO 1: getClasesPorCronograma - CORREGIDO para recibir token directamente
    suspend fun getClasesPorCronograma(token: String, idCronograma: Long): List<Clase> {
        return try {
            api.getClasesPorCronograma("Bearer $token", idCronograma)
        } catch (e: Exception) {
            Log.e("ClaseRepository", "Error al obtener clases: ${e.message}")
            emptyList()
        }
    }

    // MÉTODO 2: verificarAsistencia - CORREGIDO para recibir parámetros directamente
    suspend fun verificarAsistencia(token: String, claseId: Long, alumnoId: Long): Boolean {
        return try {
            val result = api.verificarAsistencia("Bearer $token", claseId, alumnoId)
            Log.d("AsistenciaDebug", "Resultado asistencia: $result")
            result
        } catch (e: retrofit2.HttpException) {
            Log.e("AsistenciaDebug", "Error HTTP ${e.code()} - ${e.message()}")
            false
        } catch (e: Exception) {
            Log.e("AsistenciaDebug", "Error general: ${e.message}")
            false
        }
    }

    // MÉTODO 3: registrarAsistencia - CORREGIDO para devolver Response y recibir parámetros directamente
    suspend fun registrarAsistencia(token: String, claseId: Long, alumnoId: Long): Response<ResponseBody> {
        return try {
            api.registrarAsistencia("Bearer $token", claseId, alumnoId)
        } catch (e: Exception) {
            Log.e("ClaseRepository", "Error al registrar asistencia: ${e.message}")
            throw e
        }
    }


    private suspend fun getToken(context: Context): String {
        val dataStore = DataStoreManager(context)
        val rawToken = dataStore.token.first() ?: throw Exception("Token no encontrado")
        Log.d("TokenDebug", "Token recuperado: $rawToken")
        return rawToken
    }

    private suspend fun getAlumnoId(context: Context): Long {
        val dataStore = DataStoreManager(context)
        return dataStore.userId.first() ?: throw Exception("ID de alumno no encontrado")
    }

   
    suspend fun getClasesPorCronogramaWithContext(context: Context, idCronograma: Long): List<Clase> {
        val token = getToken(context)
        return getClasesPorCronograma(token, idCronograma)
    }

    suspend fun verificarAsistenciaWithContext(context: Context, claseId: Long): Boolean {
        val token = getToken(context)
        val alumnoId = getAlumnoId(context)
        return verificarAsistencia(token, claseId, alumnoId)
    }

    suspend fun registrarAsistenciaWithContext(context: Context, claseId: Long): Response<ResponseBody> {
        val token = getToken(context)
        val alumnoId = getAlumnoId(context)
        return registrarAsistencia(token, claseId, alumnoId)
    }
}


