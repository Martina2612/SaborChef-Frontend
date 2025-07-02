package com.example.saborchef.network

import android.content.Context
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
        return dataStore.token.first() ?: throw Exception("Token no encontrado")

    }

    private suspend fun getAlumnoId(context: Context): Long {
        val dataStore = DataStoreManager(context)
        return dataStore.userId.first() ?: throw Exception("ID de alumno no encontrado")
    }



    suspend fun verificarAsistencia(context: Context, claseId: Long): Boolean {
        val token = getToken(context)
        val alumnoId = getAlumnoId(context)
        return api.verificarAsistencia(token, claseId, alumnoId)
    }

    suspend fun registrarAsistencia(context: Context, claseId: Long) {
        val token = getToken(context)
        val alumnoId = getAlumnoId(context)
        api.registrarAsistencia(token, claseId, alumnoId)
    }
}


