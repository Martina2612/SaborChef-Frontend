package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.CalificacionRequest
import com.example.saborchef.models.ComentarioRequest
import com.example.saborchef.models.ComentarioResponse
import com.example.saborchef.models.TopRecetaResponse

interface CalificacionControllerApi {
    /**
     * POST api/calificaciones/calificar
     * 
     * 
     * Responses:
     *  - 400: Bad Request
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
     *  - 400: Bad Request
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
     *  - 400: Bad Request
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
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param idReceta 
     * @return [Call]<[kotlin.Double]>
     */
    @GET("api/calificaciones/{idReceta}/promedio")
    fun obtenerPromedio(@Path("idReceta") idReceta: kotlin.Long): Call<kotlin.Double>

    /**
     * GET api/calificaciones/top
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param cantidad  (optional, default to 12)
     * @return [Call]<[kotlin.collections.List<TopRecetaResponse>]>
     */
    @GET("api/calificaciones/top")
    fun obtenerTopRecetas(@Query("cantidad") cantidad: kotlin.Int? = 12): Call<kotlin.collections.List<TopRecetaResponse>>

}
