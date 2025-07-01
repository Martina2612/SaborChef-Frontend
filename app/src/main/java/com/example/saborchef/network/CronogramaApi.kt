package com.example.saborchef.network

import com.example.saborchef.model.Cronograma
import com.example.saborchef.model.CursoInscripto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CronogramaApi {
    @GET("api/cronogramas/{id}")
    suspend fun getCronogramaPorId(@Path("id") id: Long): Response<Cronograma>
    @GET("api/cronogramas/alumno/{idAlumno}/cursos")
    suspend fun getCursosInscripto(@Path("idAlumno") idAlumno: Long): List<CursoInscripto>
}
