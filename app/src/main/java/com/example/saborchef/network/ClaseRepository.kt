package com.example.saborchef.network

import android.content.Context
import android.util.Log
import com.example.saborchef.data.DataStoreManager
import com.example.saborchef.model.Clase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.flow.first



object ClaseRepository {
    private val api: ClaseApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ClaseApi::class.java)

    suspend fun getClasesPorCronograma(context: Context, idCronograma: Long): List<Clase> {
        val token = getToken(context)
        return api.getClasesPorCronograma("Bearer $token", idCronograma)
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



    suspend fun verificarAsistencia(context: Context, claseId: Long): Boolean {
        val token = getToken(context)
        val alumnoId = getAlumnoId(context)
        Log.d("AsistenciaDebug", "Llamando asistencia con claseId=$claseId, alumnoId=$alumnoId")

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


    suspend fun registrarAsistencia(context: Context, claseId: Long) {
        val token = getToken(context)
        val alumnoId = getAlumnoId(context)
        api.registrarAsistencia(token, claseId, alumnoId)

    }
}


