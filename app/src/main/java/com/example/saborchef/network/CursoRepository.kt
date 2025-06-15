package com.example.saborchef.network

import com.example.saborchef.model.Curso
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CursoApi {
    @GET("api/cursos")
    suspend fun getCursos(): List<Curso>

    @GET("api/cursos/{id}")
    suspend fun getCursoById(@retrofit2.http.Path("id") id: Long): retrofit2.Response<Curso>

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

}
