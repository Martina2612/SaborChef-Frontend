package com.example.saborchef.apis

import retrofit2.http.*
import retrofit2.Call

import com.example.saborchef.models.Receta
import com.example.saborchef.models.RecetaDetalleResponse

interface FavoritaControllerApi {
    /**
     * POST api/favoritas/{idReceta}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     *
     * @param idReceta 
     * @return [Call]<[Void]>
     */
    @POST("api/favoritas/{idReceta}")
    fun agregar(@Path("idReceta") idReceta: Long?): Call<Void>

    /**
     * DELETE api/favoritas/{idUsuario}/{idReceta}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     *
     * @param idReceta 
     * @return [Call]<[Void]>
     */
    @DELETE("api/favoritas/{idReceta}")
    fun eliminar1(@Path("idReceta") idReceta: Long?): Call<Void>

    /**
     * GET api/favoritas/{idUsuario}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     *
     * @return [Call]<[kotlin.collections.List<Receta>]>
     */
    @GET("api/favoritas")
    fun listar(): Call<List<RecetaDetalleResponse>>

}
