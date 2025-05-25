package com.example.saborchef.network

import com.example.saborchef.model.RecetaDetalleResponse
import retrofit2.http.GET

interface RecetaApi {
    @GET("api/recetas/ultimas3")
    suspend fun ultimas3Recetas(): List<RecetaDetalleResponse>
}
