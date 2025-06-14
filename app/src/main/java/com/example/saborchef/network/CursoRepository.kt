package com.example.saborchef.network

import com.example.saborchef.model.Curso
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CursoApi {
    @GET("api/cursos")
    suspend fun getCursos(): List<Curso>
}

object CursoRepository {
    private val api: CursoApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/") // ← localhost para emulador Android
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CursoApi::class.java)

    suspend fun getAllCursos(): List<Curso> = api.getCursos()
}
