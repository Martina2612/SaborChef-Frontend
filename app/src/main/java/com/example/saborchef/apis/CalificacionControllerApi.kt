package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.CalificacionRequest
import com.example.saborchef.models.ComentarioRequest
import com.example.saborchef.models.ComentarioResponse

interface CalificacionControllerApi {
    /**
     * POST api/calificaciones/calificar
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param calificacionRequest 
     * @return [Call]<[Unit]>
     */
    @POST("api/calificaciones/calificar")
    fun calificar(@Body calificacionRequest: CalificacionRequest): Call<Unit>

    /**
     * POST api/calificaciones/comentar
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param comentarioRequest 
     * @return [Call]<[Unit]>
     */
    @POST("api/calificaciones/comentar")
    fun comentar(@Body comentarioRequest: ComentarioRequest): Call<Unit>

    /**
     * GET api/calificaciones/{idReceta}/comentarios
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param idReceta 
     * @return [Call]<[kotlin.collections.List<ComentarioResponse>]>
     */
    @GET("api/calificaciones/{idReceta}/comentarios")
    fun obtenerComentarios(@Path("idReceta") idReceta: kotlin.Long): Call<kotlin.collections.List<ComentarioResponse>>

    /**
     * GET api/calificaciones/{idReceta}/promedio
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param idReceta 
     * @return [Call]<[kotlin.Double]>
     */
    @GET("api/calificaciones/{idReceta}/promedio")
    fun obtenerPromedio(@Path("idReceta") idReceta: kotlin.Long): Call<kotlin.Double>

}
