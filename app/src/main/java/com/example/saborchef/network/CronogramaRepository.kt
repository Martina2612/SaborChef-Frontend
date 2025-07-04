package com.example.saborchef.network

import com.example.saborchef.model.Cronograma
import com.example.saborchef.model.CursoInscripto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CronogramaRepository {
    private val api: CronogramaApi = Retrofit.Builder()
        .baseUrl("https://saborchef-backend-production.up.railway.app/") // localhost para emulador
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CronogramaApi::class.java)

    suspend fun getCronogramaPorId(id: Long): Cronograma {
        val response = api.getCronogramaPorId(id)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Cronograma no encontrado")
        } else {
            throw Exception("Error HTTP ${response.code()}")
        }
    }

    suspend fun getCursosInscripto(idAlumno: Long): List<CursoInscripto> {
        return api.getCursosInscripto(idAlumno)
    }

}
