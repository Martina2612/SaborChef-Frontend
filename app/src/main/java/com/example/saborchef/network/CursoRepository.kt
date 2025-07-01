package com.example.saborchef.network

import com.example.saborchef.model.Curso
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CursoApi {
    @GET("api/cursos")
    suspend fun getCursos(): List<Curso>

    @GET("api/cursos/{id}")
    suspend fun getCursoById(@retrofit2.http.Path("id") id: Long): retrofit2.Response<Curso>

    @POST("api/cursos/{idCronograma}/{idAlumno}/inscripcion")
    suspend fun inscribirseACurso(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("idCronograma") idCronograma: Long,
        @retrofit2.http.Path("idAlumno") idAlumno: Long
    ): retrofit2.Response<Void>

    @DELETE("api/cursos/{idCronograma}/{idAlumno}/baja")
    suspend fun darseDeBaja(
        @Path("idCronograma") idCronograma: Long,
        @Path("idAlumno") idAlumno: Long,
        @Header("Authorization") token: String
    ): Response<String>




}

object CursoRepository {
    private val api: CursoApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // ← localhost para emulador Android
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CursoApi::class.java)

    suspend fun getAllCursos(): List<Curso> = api.getCursos()

    suspend fun getCursoPorId(id: Long): Curso {
        val response = api.getCursoById(id)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Curso no encontrado")
        } else {
            throw Exception("Error HTTP ${response.code()}")
        }
    }

    suspend fun inscribirseACurso(token: String, idCronograma: Long, idAlumno: Long): retrofit2.Response<Void> {
        return api.inscribirseACurso("Bearer $token", idCronograma, idAlumno)
    }

    suspend fun darseDeBaja(token: String, idCronograma: Long, idAlumno: Long): Response<String> {
        return api.darseDeBaja(idCronograma, idAlumno, "Bearer $token")
    }




}
