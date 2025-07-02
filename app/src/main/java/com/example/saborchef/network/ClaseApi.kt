package com.example.saborchef.network

import com.example.saborchef.model.Clase
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ClaseApi {
    @GET("api/clases/cronograma/{idCronograma}")
    suspend fun getClasesPorCronograma(
        @Header("Authorization") token: String,
        @Path("idCronograma") idCronograma: Long
    ): List<Clase>

    @GET("api/clases/{claseId}/asistencia")
    suspend fun verificarAsistencia(
        @Header("Authorization") token: String,
        @Path("claseId") claseId: Long,
        @Query("alumnoId") alumnoId: Long
    ): Boolean

    @POST("api/clases/{claseId}/asistencia")
    suspend fun registrarAsistencia(
        @Header("Authorization") token: String,
        @Path("claseId") claseId: Long,
        @Query("alumnoId") alumnoId: Long
    ): ResponseBody
}


