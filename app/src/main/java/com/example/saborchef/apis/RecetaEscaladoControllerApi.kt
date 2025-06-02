package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.RecetaEscaladaResponse

interface RecetaEscaladoControllerApi {
    /**
     * DELETE api/recetas/guardadas/{idGuardada}
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param idGuardada 
     * @return [Call]<[Unit]>
     */
    @DELETE("api/recetas/guardadas/{idGuardada}")
    fun eliminarGuardada(@Path("idGuardada") idGuardada: kotlin.Long): Call<Unit>

    /**
     * GET api/recetas/{id}/escalar
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @param factor 
     * @return [Call]<[RecetaEscaladaResponse]>
     */
    @GET("api/recetas/{id}/escalar")
    fun escalarPorFactor(@Path("id") id: kotlin.Long, @Query("factor") factor: kotlin.Double): Call<RecetaEscaladaResponse>

    /**
     * GET api/recetas/{id}/escalar/porIngrediente
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @param ingredienteId 
     * @param cantidad 
     * @return [Call]<[RecetaEscaladaResponse]>
     */
    @GET("api/recetas/{id}/escalar/porIngrediente")
    fun escalarPorIngrediente(@Path("id") id: kotlin.Long, @Query("ingredienteId") ingredienteId: kotlin.Long, @Query("cantidad") cantidad: kotlin.Double): Call<RecetaEscaladaResponse>

    /**
     * GET api/recetas/{id}/escalar/porciones
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @param porcionesDeseadas 
     * @return [Call]<[RecetaEscaladaResponse]>
     */
    @GET("api/recetas/{id}/escalar/porciones")
    fun escalarPorPorciones(@Path("id") id: kotlin.Long, @Query("porcionesDeseadas") porcionesDeseadas: kotlin.Int): Call<RecetaEscaladaResponse>

    /**
     * POST api/recetas/{id}/guardar
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @param id 
     * @param factor  (optional)
     * @param porcionesDeseadas  (optional)
     * @param ingredienteId  (optional)
     * @param cantidad  (optional)
     * @return [Call]<[Unit]>
     */
    @POST("api/recetas/{id}/guardar")
    fun guardarRecetaEscalada(@Path("id") id: kotlin.Long, @Query("factor") factor: kotlin.Double? = null, @Query("porcionesDeseadas") porcionesDeseadas: kotlin.Int? = null, @Query("ingredienteId") ingredienteId: kotlin.Long? = null, @Query("cantidad") cantidad: kotlin.Double? = null): Call<Unit>

    /**
     * GET api/recetas/guardadas
     * 
     * 
     * Responses:
     *  - 400: Bad Request
     *  - 200: OK
     *
     * @return [Call]<[kotlin.collections.List<RecetaEscaladaResponse>]>
     */
    @GET("api/recetas/guardadas")
    fun listarGuardadas(): Call<kotlin.collections.List<RecetaEscaladaResponse>>

}
