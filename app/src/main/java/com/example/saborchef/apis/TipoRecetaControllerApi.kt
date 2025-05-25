package com.example.saborchef.apis

import com.example.saborchef.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName


interface TipoRecetaControllerApi {
    /**
     * GET api/tipos-receta
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.collections.List<kotlin.String>]>
     */
    @GET("api/tipos-receta")
    fun listarTipos(): Call<kotlin.collections.List<kotlin.String>>

}
