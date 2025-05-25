package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.example.saborchef.models.Sede

interface SedeControllerApi {
    /**
     * POST api/sedes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param sede 
     * @return [Call]<[Sede]>
     */
    @POST("api/sedes")
    fun crearSede(@Body sede: Sede): Call<Sede>

    /**
     * GET api/sedes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.collections.List<Sede>]>
     */
    @GET("api/sedes")
    fun listarSedes(): Call<kotlin.collections.List<Sede>>

    /**
     * GET api/sedes/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[Sede]>
     */
    @GET("api/sedes/{id}")
    fun obtenerSedePorId(@Path("id") id: kotlin.Long): Call<Sede>

}
