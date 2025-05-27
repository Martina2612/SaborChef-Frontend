package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.Receta

interface FavoritaControllerApi {
    /**
     * POST api/favoritas/{idUsuario}/{idReceta}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param idUsuario 
     * @param idReceta 
     * @return [Call]<[kotlin.Any]>
     */
    @POST("api/favoritas/{idUsuario}/{idReceta}")
    fun agregar(@Path("idUsuario") idUsuario: kotlin.Long, @Path("idReceta") idReceta: kotlin.Long): Call<kotlin.Any>

    /**
     * DELETE api/favoritas/{idUsuario}/{idReceta}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param idUsuario 
     * @param idReceta 
     * @return [Call]<[kotlin.Any]>
     */
    @DELETE("api/favoritas/{idUsuario}/{idReceta}")
    fun eliminar1(@Path("idUsuario") idUsuario: kotlin.Long, @Path("idReceta") idReceta: kotlin.Long): Call<kotlin.Any>

    /**
     * GET api/favoritas/{idUsuario}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param idUsuario 
     * @return [Call]<[kotlin.collections.List<Receta>]>
     */
    @GET("api/favoritas/{idUsuario}")
    fun listar(@Path("idUsuario") idUsuario: kotlin.Long): Call<kotlin.collections.List<Receta>>

}
