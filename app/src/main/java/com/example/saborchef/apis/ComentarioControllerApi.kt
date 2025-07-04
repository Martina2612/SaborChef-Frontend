package com.example.saborchef.apis

import retrofit2.Call
import retrofit2.http.*
import com.example.saborchef.models.ComentarioRequest
import com.example.saborchef.models.ComentarioResponse

interface ComentarioControllerApi {

    /**
     * GET api/comentarios/{idReceta}
     *
     * Lista todos los comentarios habilitados para una receta
     *
     * @param idReceta ID de la receta
     * @return Call<List<ComentarioResponse>>
     */
    @GET("api/comentarios/{idReceta}")
    fun obtenerComentarios(
        @Path("idReceta") idReceta: Long
    ): Call<List<ComentarioResponse>>

    /**
     * POST api/comentarios
     *
     * Agrega un nuevo comentario (habilitado=true por defecto)
     *
     * @param comentarioRequest cuerpo con idReceta y texto
     * @return Call<ComentarioResponse>
     */
    @POST("api/comentarios")
    fun crearComentario(
        @Body comentarioRequest: ComentarioRequest
    ): Call<ComentarioResponse>
}
