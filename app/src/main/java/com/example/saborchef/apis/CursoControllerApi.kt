package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.CursoDisponibleDTO

interface CursoControllerApi {
    /**
     * POST api/cursos/cursos/{idCronograma}/{idAlumno}/inscripcion
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param idCronograma 
     * @param idAlumno 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/cursos/cursos/{idCronograma}/{idAlumno}/inscripcion")
    fun inscribirAlumno(@Path("idCronograma") idCronograma: kotlin.Long, @Path("idAlumno") idAlumno: kotlin.Long): Call<kotlin.String>

    /**
     * GET api/cursos/cursos/{id}/alumnos
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[kotlin.collections.List<kotlin.String>]>
     */
    @GET("api/cursos/cursos/{id}/alumnos")
    fun listarAlumnosInscritos(@Path("id") id: kotlin.Long): Call<kotlin.collections.List<kotlin.String>>

    /**
     * GET api/cursos
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @return [Call]<[kotlin.collections.List<CursoDisponibleDTO>]>
     */
    @GET("api/cursos")
    fun listarCursosDisponibles(): Call<kotlin.collections.List<CursoDisponibleDTO>>

    /**
     * GET api/cursos/{id}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[CursoDisponibleDTO]>
     */
    @GET("api/cursos/{id}")
    fun obtenerCursoPorId(@Path("id") id: kotlin.Long): Call<CursoDisponibleDTO>

    /**
     * POST api/cursos/{id}/asistencia
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @param alumnoId 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/cursos/{id}/asistencia")
    fun registrarAsistencia(@Path("id") id: kotlin.Long, @Query("alumnoId") alumnoId: kotlin.Long): Call<kotlin.String>

}
