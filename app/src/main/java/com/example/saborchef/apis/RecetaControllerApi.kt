package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.RecetaCrearRequest
import com.example.saborchef.models.RecetaDetalleResponse
import com.example.saborchef.models.RecetaFiltroRequest
import com.example.saborchef.models.RecetaResumenResponse

interface RecetaControllerApi {
    /**
     * PUT api/recetas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param recetaCrearRequest 
     * @return [Call]<[Unit]>
     */
    @PUT("api/recetas/{id}")
    fun actualizar(@Path("id") id: kotlin.Long, @Body recetaCrearRequest: RecetaCrearRequest): Call<Unit>

    /**
     * POST api/recetas/buscar
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param recetaFiltroRequest 
     * @return [Call]<[kotlin.collections.List<RecetaResumenResponse>]>
     */
    @POST("api/recetas/buscar")
    fun buscarPorFiltros(@Body recetaFiltroRequest: RecetaFiltroRequest): Call<kotlin.collections.List<RecetaResumenResponse>>

    /**
     * GET api/recetas/ingrediente
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param nombreIngrediente 
     * @param orden  (optional)
     * @return [Call]<[kotlin.collections.List<RecetaResumenResponse>]>
     */
    @GET("api/recetas/ingrediente")
    fun buscarPorIngrediente(@Query("nombreIngrediente") nombreIngrediente: kotlin.String, @Query("orden") orden: kotlin.String? = null): Call<kotlin.collections.List<RecetaResumenResponse>>

    /**
     * GET api/recetas/search
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param nombre 
     * @param orden  (optional)
     * @return [Call]<[kotlin.collections.List<RecetaResumenResponse>]>
     */
    @GET("api/recetas/search")
    fun buscarPorNombre(@Query("nombre") nombre: kotlin.String, @Query("orden") orden: kotlin.String? = null): Call<kotlin.collections.List<RecetaResumenResponse>>

    /**
     * GET api/recetas/tipo/{tipo}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param tipo 
     * @param orden  (optional)
     * @return [Call]<[kotlin.collections.List<RecetaResumenResponse>]>
     */
    @GET("api/recetas/tipo/{tipo}")
    fun buscarPorTipo(@Path("tipo") tipo: kotlin.String, @Query("orden") orden: kotlin.String? = null): Call<kotlin.collections.List<RecetaResumenResponse>>

    /**
     * GET api/recetas/buscar/usuario
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param nombre 
     * @param orden  (optional)
     * @return [Call]<[kotlin.collections.List<RecetaResumenResponse>]>
     */
    @GET("api/recetas/buscar/usuario")
    fun buscarPorUsuario(@Query("nombre") nombre: kotlin.String, @Query("orden") orden: kotlin.String? = null): Call<kotlin.collections.List<RecetaResumenResponse>>

    /**
     * GET api/recetas/sin-ingrediente
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param nombreIngrediente 
     * @param orden  (optional)
     * @return [Call]<[kotlin.collections.List<RecetaResumenResponse>]>
     */
    @GET("api/recetas/sin-ingrediente")
    fun buscarSinIngrediente(@Query("nombreIngrediente") nombreIngrediente: kotlin.String, @Query("orden") orden: kotlin.String? = null): Call<kotlin.collections.List<RecetaResumenResponse>>

    /**
     * POST api/recetas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param recetaCrearRequest 
     * @return [Call]<[Unit]>
     */
    @POST("api/recetas")
    fun crearReceta(@Body recetaCrearRequest: RecetaCrearRequest): Call<Unit>

    /**
     * DELETE api/recetas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[Unit]>
     */
    @DELETE("api/recetas/{id}")
    fun eliminar(@Path("id") id: kotlin.Long): Call<Unit>

    /**
     * GET api/recetas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param idUsuario  (optional)
     * @param idTipo  (optional)
     * @param orden  (optional)
     * @return [Call]<[kotlin.collections.List<RecetaResumenResponse>]>
     */
    @GET("api/recetas")
    fun listarRecetas(@Query("idUsuario") idUsuario: kotlin.Long? = null, @Query("idTipo") idTipo: kotlin.Long? = null, @Query("orden") orden: kotlin.String? = null): Call<kotlin.collections.List<RecetaResumenResponse>>

    /**
     * GET api/recetas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[RecetaDetalleResponse]>
     */
    @GET("api/recetas/{id}")
    fun obtener(@Path("id") id: kotlin.Long): Call<RecetaDetalleResponse>

    /**
     * GET api/recetas/ultimas3
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.collections.List<RecetaDetalleResponse>]>
     */
    @GET("api/recetas/ultimas3")
    fun obtenerUltimas3Recetas(): Call<kotlin.collections.List<RecetaDetalleResponse>>

}
